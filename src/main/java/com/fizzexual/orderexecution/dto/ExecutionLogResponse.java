package com.fizzexual.orderexecution.dto;

import com.fizzexual.orderexecution.entity.ExecutionLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionLogResponse {

    private Long id;
    private Long orderId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private ExecutionLog.ExecutionStatus status;
    private String message;
    private LocalDateTime executedAt;
}
