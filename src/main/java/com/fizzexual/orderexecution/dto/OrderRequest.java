package com.fizzexual.orderexecution.dto;

import com.fizzexual.orderexecution.entity.Order;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotBlank(message = "Symbol is required")
    @Size(max = 10, message = "Symbol must not exceed 10 characters")
    private String symbol;

    @NotNull(message = "Order type is required")
    private Order.OrderType type;

    @NotNull(message = "Order side is required")
    private Order.OrderSide side;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000000, message = "Quantity must not exceed 1,000,000")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Limit price must be at least 0.01")
    private BigDecimal limitPrice;
}
