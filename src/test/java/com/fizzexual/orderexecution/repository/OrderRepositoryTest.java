package com.fizzexual.orderexecution.repository;

import com.fizzexual.orderexecution.entity.Account;
import com.fizzexual.orderexecution.entity.Order;
import com.fizzexual.orderexecution.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .status(User.UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        testAccount = Account.builder()
                .accountNumber("ACC-10001")
                .user(user)
                .balance(new BigDecimal("100000.00"))
                .availableBalance(new BigDecimal("100000.00"))
                .status(Account.AccountStatus.ACTIVE)
                .build();
        testAccount = accountRepository.save(testAccount);
    }

    @Test
    void saveOrder_Success() {
        Order order = Order.builder()
                .orderNumber("ORD-12345678")
                .account(testAccount)
                .symbol("AAPL")
                .type(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(100)
                .status(Order.OrderStatus.PENDING)
                .executedQuantity(0)
                .build();

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder.getId());
        assertEquals("ORD-12345678", savedOrder.getOrderNumber());
        assertEquals("AAPL", savedOrder.getSymbol());
        assertNotNull(savedOrder.getCreatedAt());
    }

    @Test
    void findByOrderNumber_Success() {
        Order order = Order.builder()
                .orderNumber("ORD-TEST-001")
                .account(testAccount)
                .symbol("GOOGL")
                .type(Order.OrderType.LIMIT)
                .side(Order.OrderSide.SELL)
                .quantity(50)
                .limitPrice(new BigDecimal("150.00"))
                .status(Order.OrderStatus.PENDING)
                .executedQuantity(0)
                .build();
        orderRepository.save(order);

        Optional<Order> found = orderRepository.findByOrderNumber("ORD-TEST-001");

        assertTrue(found.isPresent());
        assertEquals("GOOGL", found.get().getSymbol());
    }

    @Test
    void findByAccountId_Success() {
        Order order1 = createTestOrder("ORD-001", "AAPL");
        Order order2 = createTestOrder("ORD-002", "GOOGL");
        orderRepository.save(order1);
        orderRepository.save(order2);

        List<Order> orders = orderRepository.findByAccountId(testAccount.getId());

        assertEquals(2, orders.size());
    }

    @Test
    void findBySymbol_Success() {
        Order order1 = createTestOrder("ORD-001", "AAPL");
        Order order2 = createTestOrder("ORD-002", "AAPL");
        Order order3 = createTestOrder("ORD-003", "GOOGL");
        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);

        List<Order> orders = orderRepository.findBySymbol("AAPL");

        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(o -> "AAPL".equals(o.getSymbol())));
    }

    private Order createTestOrder(String orderNumber, String symbol) {
        return Order.builder()
                .orderNumber(orderNumber)
                .account(testAccount)
                .symbol(symbol)
                .type(Order.OrderType.MARKET)
                .side(Order.OrderSide.BUY)
                .quantity(100)
                .status(Order.OrderStatus.PENDING)
                .executedQuantity(0)
                .build();
    }
}
