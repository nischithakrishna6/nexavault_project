package com.fintech.platform.dto;

import com.fintech.platform.model.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private Account.AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private Boolean isActive;
    private LocalDateTime createdAt;
}