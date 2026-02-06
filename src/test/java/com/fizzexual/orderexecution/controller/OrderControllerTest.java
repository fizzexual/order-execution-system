package com.fizzexual.orderexecution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizzexual.orderexecution.dto.OrderRequest;
import com.fizzexual.orderexecution.dto.OrderResponse;
import com.fizzexual.orderexecution.entity.Order;
import com.fizzexual.orderexecution.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_Success() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .accountId(1L)
                .symbol("AAPL")
                .type(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(100)
                .build();

        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .accountId(1L)
                .symbol("AAPL")
                .type(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(100)
                .status(Order.OrderStatus.EXECUTED)
                .createdAt(LocalDateTime.now())
                .build();

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-12345678"))
                .andExpect(jsonPath("$.symbol").value("AAPL"));
    }

    @Test
    void createOrder_ValidationError() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .symbol("AAPL")
                .type(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(100)
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderById_Success() throws Exception {
        // Arrange
        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .symbol("AAPL")
                .build();

        when(orderService.getOrderById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-12345678"));
    }

    @Test
    void getAllOrders_Success() throws Exception {
        // Arrange
        List<OrderResponse> orders = Arrays.asList(
                OrderResponse.builder().id(1L).orderNumber("ORD-001").build(),
                OrderResponse.builder().id(2L).orderNumber("ORD-002").build()
        );

        when(orderService.getAllOrders()).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void cancelOrder_Success() throws Exception {
        // Arrange
        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .orderNumber("ORD-12345678")
                .status(Order.OrderStatus.CANCELLED)
                .build();

        when(orderService.cancelOrder(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
