package com.flower.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NotificationService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId) {
        // 타임아웃 1시간
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));
        emitter.onError((e) -> emitters.remove(memberId));

        // 503 에러 방지를 위한 더미 데이터 전송
        try {
            emitter.send(SseEmitter.event().name("connect").data("connected"));
        } catch (IOException e) {
            log.error("SSE 연결 실패", e);
        }

        return emitter;
    }

    public void send(Long memberId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
                log.info("알림 전송 성공: MemberId={}, Event={}", memberId, eventName);
            } catch (IOException e) {
                emitters.remove(memberId);
                log.error("알림 전송 실패: MemberId={}", memberId);
            }
        }
    }
}
