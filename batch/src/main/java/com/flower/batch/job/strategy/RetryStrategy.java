package com.flower.batch.job.strategy;

import com.flower.common.entity.FailureLog;

public interface RetryStrategy {
    /**
     * 이 전략이 해당 도메인을 처리할 수 있는지 확인
     */
    boolean supports(String domain);

    /**
     * 재시도 로직 수행
     * 성공 시 true 반환, 실패 시 예외 발생 또는 false 반환 (예외 권장)
     */
    void retry(FailureLog log);
}
