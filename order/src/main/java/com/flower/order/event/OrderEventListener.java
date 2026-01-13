package com.flower.order.event;

import com.flower.common.entity.FailureLog;
import com.flower.common.event.InventoryDeductionFailedEvent;
import com.flower.common.repository.FailureLogRepository;
import com.flower.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderService orderService;
    private final FailureLogRepository failureLogRepository;

    /**
     * 재고 차감 실패 이벤트 처리
     * 주문 취소(보상 트랜잭션) 수행
     */
    @Async
    @EventListener
    public void handleInventoryDeductionFailed(InventoryDeductionFailedEvent event) {
        log.warn("재고 차감 실패 이벤트 수신 - 주문 취소 진행: 주문번호={}, 사유={}", 
                event.getOrderNumber(), event.getReason());
        
        try {
            orderService.cancelOrder(event.getOrderNumber(), event.getReason());
        } catch (Exception e) {
            log.error("주문 취소 실패 (치명적 오류): 주문번호={}, 오류={}", event.getOrderNumber(), e.getMessage());
            
            // 최후의 안전장치: DB에 실패 로그 기록
            FailureLog failureLog = FailureLog.builder()
                    .domain("ORDER")
                    .referenceId(event.getOrderNumber())
                    .errorMessage(e.getMessage())
                    .payload("Reason: " + event.getReason())
                    .build();
            
            try {
                failureLogRepository.save(failureLog);
                log.info("실패 로그 저장 완료: {}", failureLog.getId());
            } catch (Exception dbException) {
                // 정말 최악의 상황 (DB도 죽음)
                log.error("실패 로그 저장 실패: {}", dbException.getMessage());
            }
        }
    }
}
