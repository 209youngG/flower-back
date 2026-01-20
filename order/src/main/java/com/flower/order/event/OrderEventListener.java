package com.flower.order.event;

import com.flower.common.repository.FailureLogRepository;
import com.flower.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderService orderService;
    private final FailureLogRepository failureLogRepository;

}
