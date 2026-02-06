package com.fizzexual.orderexecution.mapper;

import com.fizzexual.orderexecution.dto.ExecutionLogResponse;
import com.fizzexual.orderexecution.entity.ExecutionLog;
import org.springframework.stereotype.Component;

@Component
public class ExecutionLogMapper {

    public ExecutionLogResponse toResponse(ExecutionLog log) {
        return ExecutionLogResponse.builder()
                .id(log.getId())
                .orderId(log.getOrder().getId())
                .quantity(log.getQuantity())
                .price(log.getPrice())
                .totalAmount(log.getTotalAmount())
                .status(log.getStatus())
                .message(log.getMessage())
                .executedAt(log.getExecutedAt())
                .build();
    }
}
