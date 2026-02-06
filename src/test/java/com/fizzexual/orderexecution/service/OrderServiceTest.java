package com.fizzexual.orderexecution.service;

import com.fizzexual.orderexecution.dto.OrderRequest;
import com.fizzexual.orderexecution.dto.OrderResponse;
import com.fizzexual.orderexecution.entity.Account;
import com.fizzexual.orderexecution.entity.Order;
import com.fizzexual.orderexecution.entity.User;
import com.fizzexual.orderexecution.exception.BusinessException;
import com.fizzexual.orderexecution.exception.ResourceNotFoundException;
import com.fizzexual.orderexecution.mapper.OrderMapper;
import com.fizzexual.orderexecution.repository.AccountRepository;
import com.fizzexual.orderexecution.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OrderExecutionService orderExecutionService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Account testAccount;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .status(User.UserStatus.ACTIVE)
                .build();

        testAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC-10001")
                .user(testUser)
                .balance(new BigDecimal("100000.00"))
                .availableBalance(new BigDecimal("100000.00"))
                .status(Account.AccountStatus.ACTIVE)
                .build();

        orderRequest = OrderRequest.builder()
                .accountId(1L)
                .symbol("AAPL")
                .type(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(100)
                .build();
    }

    @Test
    void createOrder_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        
        Order savedOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .account(testAccount)
                .symbol("AAPL")
                .type(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(100)
                .status(Order.OrderStatus.PENDING)
                .build();
        
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        
        OrderResponse expectedResponse = OrderResponse.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .build();
        
        when(orderMapper.toResponse(any(Order.class))).thenReturn(expectedResponse);

        OrderResponse result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        verify(accountRepository).findById(1L);
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(orderExecutionService).executeOrder(any(Order.class), any(Account.class));
    }

    @Test
    void createOrder_AccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderRequest));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_InactiveAccount() {
        testAccount.setStatus(Account.AccountStatus.FROZEN);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        assertThrows(BusinessException.class, () -> orderService.createOrder(orderRequest));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_Success() {
        Order order = Order.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .status(Order.OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(new OrderResponse());

        OrderResponse result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_AlreadyExecuted() {
        Order order = Order.builder()
                .id(1L)
                .status(Order.OrderStatus.EXECUTED)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.cancelOrder(1L));
    }
}
