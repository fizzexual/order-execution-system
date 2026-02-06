package com.fizzexual.orderexecution.dto;

import com.fizzexual.orderexecution.entity.Account;
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
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private Long userId;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private Account.AccountStatus status;
    private LocalDateTime createdAt;
}
