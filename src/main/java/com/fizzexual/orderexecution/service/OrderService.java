package com.fizzexual.orderexecution.service;

import com.fizzexual.orderexecution.dto.OrderRequest;
import com.fizzexual.orderexecution.dto.OrderResponse;
import com.fizzexual.orderexecution.entity.Account;
import com.fizzexual.orderexecution.entity.Order;
import com.fizzexual.orderexecution.exception.BusinessException;
import com.fizzexual.orderexecution.exception.ResourceNotFoundException;
import com.fizzexual.orderexecution.mapper.OrderMapper;
import com.fizzexual.orderexecution.repository.AccountRepository;
import com.fizzexual.orderexecution.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final OrderExecutionService orderExecutionService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for account: {}", request.getAccountId());

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));

        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BusinessException("Cannot create order for inactive account");
        }

        if (request.getType() == Order.OrderType.LIMIT && request.getLimitPrice() == null) {
            throw new BusinessException("Limit price is required for LIMIT orders");
        }

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .account(account)
                .symbol(request.getSymbol().toUpperCase())
                .type(request.getType())
                .side(request.getSide())
                .quantity(request.getQuantity())
                .limitPrice(request.getLimitPrice())
                .status(Order.OrderStatus.PENDING)
                .executedQuantity(0)
                .build();

        order = orderRepository.save(order);
        log.info("Order created with number: {}", order.getOrderNumber());

        try {
            orderExecutionService.executeOrder(order, account);
            order = orderRepository.save(order);
        } catch (BusinessException e) {
            log.error("Order execution failed: {}", e.getMessage());
            order = orderRepository.save(order);
        }

        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByAccountId(Long accountId) {
        List<Order> orders = orderRepository.findByAccountId(accountId);
        return orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (order.getStatus() == Order.OrderStatus.EXECUTED) {
            throw new BusinessException("Cannot cancel an executed order");
        }

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BusinessException("Order is already cancelled");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        log.info("Order {} cancelled", order.getOrderNumber());

        return orderMapper.toResponse(order);
    }

    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
