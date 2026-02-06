package com.fizzexual.orderexecution.mapper;

import com.fizzexual.orderexecution.dto.OrderResponse;
import com.fizzexual.orderexecution.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .accountId(order.getAccount().getId())
                .symbol(order.getSymbol())
                .type(order.getType())
                .side(order.getSide())
                .quantity(order.getQuantity())
                .limitPrice(order.getLimitPrice())
                .status(order.getStatus())
                .executedPrice(order.getExecutedPrice())
                .executedQuantity(order.getExecutedQuantity())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
