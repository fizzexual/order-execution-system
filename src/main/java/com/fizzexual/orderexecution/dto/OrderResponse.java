package com.fizzexual.orderexecution.dto;

import com.fizzexual.orderexecution.entity.Order;
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
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private Long accountId;
    private String symbol;
    private Order.OrderType type;
    private Order.OrderSide side;
    private Integer quantity;
    private BigDecimal limitPrice;
    private Order.OrderStatus status;
    private BigDecimal executedPrice;
    private Integer executedQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
