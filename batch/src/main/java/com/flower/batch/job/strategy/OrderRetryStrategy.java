package com.flower.batch.job.strategy;

import com.flower.common.entity.FailureLog;
import com.flower.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRetryStrategy implements RetryStrategy {

    private final OrderService orderService;

    @Override
    public boolean supports(String domain) {
        return "ORDER".equals(domain);
    }

    @Override
    public void retry(FailureLog logItem) {
        String reason = extractReason(logItem.getPayload());
        log.info("주문 보상 트랜잭션 재시도 실행: 주문번호={}", logItem.getReferenceId());
        
        // 실패하면 예외가 발생하여 상위 스케줄러에서 catch됨
        orderService.cancelOrder(logItem.getReferenceId(), reason + " (Retry)");
    }

    private String extractReason(String payload) {
        if (payload != null && payload.startsWith("Reason: ")) {
            return payload.substring("Reason: ".length());
        }
        return "Unknown reason";
    }
}
