package com.flower.common.repository;

import com.flower.common.entity.FailureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailureLogRepository extends JpaRepository<FailureLog, Long> {
    List<FailureLog> findByStatus(FailureLog.ProcessingStatus status);
}
