package com.fizzexual.orderexecution.repository;

import com.fizzexual.orderexecution.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByAccountId(Long accountId);
    
    List<Order> findBySymbol(String symbol);
    
    @Query("SELECT o FROM Order o WHERE o.account.id = :accountId AND o.status = :status")
    List<Order> findByAccountIdAndStatus(@Param("accountId") Long accountId, 
                                         @Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
}
