package com.fizzexual.orderexecution.config;

import com.fizzexual.orderexecution.entity.Account;
import com.fizzexual.orderexecution.entity.User;
import com.fizzexual.orderexecution.repository.AccountRepository;
import com.fizzexual.orderexecution.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    @Profile("dev")
    public CommandLineRunner initializeData(UserRepository userRepository, 
                                           AccountRepository accountRepository) {
        return args -> {
            log.info("Initializing test data...");

            User user1 = User.builder()
                    .name("John Doe")
                    .email("john.doe@example.com")
                    .status(User.UserStatus.ACTIVE)
                    .build();
            user1 = userRepository.save(user1);

            User user2 = User.builder()
                    .name("Jane Smith")
                    .email("jane.smith@example.com")
                    .status(User.UserStatus.ACTIVE)
                    .build();
            user2 = userRepository.save(user2);

            Account account1 = Account.builder()
                    .accountNumber("ACC-10001")
                    .user(user1)
                    .balance(new BigDecimal("100000.00"))
                    .availableBalance(new BigDecimal("100000.00"))
                    .status(Account.AccountStatus.ACTIVE)
                    .build();
            accountRepository.save(account1);

            Account account2 = Account.builder()
                    .accountNumber("ACC-10002")
                    .user(user1)
                    .balance(new BigDecimal("50000.00"))
                    .availableBalance(new BigDecimal("50000.00"))
                    .status(Account.AccountStatus.ACTIVE)
                    .build();
            accountRepository.save(account2);

            Account account3 = Account.builder()
                    .accountNumber("ACC-10003")
                    .user(user2)
                    .balance(new BigDecimal("75000.00"))
                    .availableBalance(new BigDecimal("75000.00"))
                    .status(Account.AccountStatus.ACTIVE)
                    .build();
            accountRepository.save(account3);

            log.info("Test data initialized successfully");
        };
    }
}
