package com.fizzexual.orderexecution.service;

import com.fizzexual.orderexecution.entity.Account;
import com.fizzexual.orderexecution.entity.ExecutionLog;
import com.fizzexual.orderexecution.entity.Order;
import com.fizzexual.orderexecution.exception.BusinessException;
import com.fizzexual.orderexecution.repository.ExecutionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderExecutionService {

    private final ExecutionLogRepository executionLogRepository;
    private final Random random = new Random();

    @Transactional
    public void executeOrder(Order order, Account account) {
        log.info("Starting execution for order: {}", order.getOrderNumber());

        try {
            validateOrderExecution(order, account);
            BigDecimal executionPrice = calculateExecutionPrice(order);
            BigDecimal totalCost = executionPrice.multiply(BigDecimal.valueOf(order.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            if (order.getSide() == Order.OrderSide.BUY) {
                if (account.getAvailableBalance().compareTo(totalCost) < 0) {
                    throw new BusinessException("Insufficient balance for order execution");
                }
                account.setAvailableBalance(account.getAvailableBalance().subtract(totalCost));
            } else {
                account.setAvailableBalance(account.getAvailableBalance().add(totalCost));
            }

            order.setStatus(Order.OrderStatus.EXECUTED);
            order.setExecutedPrice(executionPrice);
            order.setExecutedQuantity(order.getQuantity());

            ExecutionLog executionLog = ExecutionLog.builder()
                    .order(order)
                    .quantity(order.getQuantity())
                    .price(executionPrice)
                    .totalAmount(totalCost)
                    .status(ExecutionLog.ExecutionStatus.SUCCESS)
                    .message("Order executed successfully")
                    .build();

            executionLogRepository.save(executionLog);
            log.info("Order {} executed successfully at price {}", order.getOrderNumber(), executionPrice);

        } catch (BusinessException e) {
            order.setStatus(Order.OrderStatus.REJECTED);
            ExecutionLog failedLog = ExecutionLog.builder()
                    .order(order)
                    .quantity(0)
                    .price(BigDecimal.ZERO)
                    .totalAmount(BigDecimal.ZERO)
                    .status(ExecutionLog.ExecutionStatus.FAILED)
                    .message(e.getMessage())
                    .build();
            executionLogRepository.save(failedLog);
            log.error("Order {} execution failed: {}", order.getOrderNumber(), e.getMessage());
            throw e;
        }
    }

    private void validateOrderExecution(Order order, Account account) {
        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BusinessException("Account is not active");
        }

        if (order.getType() == Order.OrderType.LIMIT && order.getLimitPrice() == null) {
            throw new BusinessException("Limit price is required for LIMIT orders");
        }

        if (order.getQuantity() <= 0) {
            throw new BusinessException("Order quantity must be positive");
        }
    }

    private BigDecimal calculateExecutionPrice(Order order) {
        if (order.getType() == Order.OrderType.MARKET) {
            return generateMarketPrice();
        } else {
            return order.getLimitPrice();
        }
    }

    private BigDecimal generateMarketPrice() {
        double basePrice = 100.0;
        double variation = (random.nextDouble() - 0.5) * 20;
        return BigDecimal.valueOf(basePrice + variation).setScale(2, RoundingMode.HALF_UP);
    }
}
