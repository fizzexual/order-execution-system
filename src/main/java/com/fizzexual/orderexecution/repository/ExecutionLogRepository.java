package com.fizzexual.orderexecution.repository;

import com.fizzexual.orderexecution.entity.ExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutionLogRepository extends JpaRepository<ExecutionLog, Long> {
    
    List<ExecutionLog> findByOrderId(Long orderId);
    
    List<ExecutionLog> findByOrderIdOrderByExecutedAtDesc(Long orderId);
}
