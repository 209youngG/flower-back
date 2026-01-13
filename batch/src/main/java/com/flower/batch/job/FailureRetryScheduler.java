package com.flower.batch.job;

import com.flower.batch.job.strategy.RetryStrategy;
import com.flower.common.entity.FailureLog;
import com.flower.common.repository.FailureLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FailureRetryScheduler {

    private final FailureLogRepository failureLogRepository;
    private final List<RetryStrategy> strategies;

    /**
     * 실패한 보상 트랜잭션(주문 취소) 재시도
     * 1분마다 실행
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void retryFailedCancellations() {
        log.info("보상 트랜잭션 재시도 배치 시작...");
        
        List<FailureLog> pendingLogs = failureLogRepository.findByStatus(FailureLog.ProcessingStatus.PENDING);
        
        if (pendingLogs.isEmpty()) {
            log.info("처리할 대기 로그가 없습니다.");
            return;
        }

        for (FailureLog logItem : pendingLogs) {
            processLog(logItem);
        }
        
        log.info("보상 트랜잭션 재시도 배치 종료.");
    }

    private void processLog(FailureLog logItem) {
        if (logItem.getRetryCount() >= 5) {
            log.warn("최대 재시도 횟수 초과. 상태를 FAILED로 변경합니다. ID={}, Ref={}", logItem.getId(), logItem.getReferenceId());
            logItem.markAsFailed();
            failureLogRepository.save(logItem);
            return;
        }

        RetryStrategy strategy = strategies.stream()
                .filter(s -> s.supports(logItem.getDomain()))
                .findFirst()
                .orElse(null);

        if (strategy == null) {
            log.warn("지원하지 않는 도메인입니다: {}. 처리를 건너뜁니다.", logItem.getDomain());
            logItem.incrementRetryCount();
            failureLogRepository.save(logItem);
            return;
        }

        try {
            log.info("재시도 수행 중... ID={}, Ref={}, Count={}", logItem.getId(), logItem.getReferenceId(), logItem.getRetryCount() + 1);
            
            strategy.retry(logItem);
            
            logItem.markAsResolved();
            log.info("재시도 성공. 상태를 RESOLVED로 변경합니다. ID={}", logItem.getId());

        } catch (Exception e) {
            log.error("재시도 실패. ID={}, Error={}", logItem.getId(), e.getMessage());
            logItem.incrementRetryCount();
        }
        
        failureLogRepository.save(logItem);
    }
}
