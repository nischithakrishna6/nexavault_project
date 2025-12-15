package com.fintech.platform.dto;

import com.fintech.platform.model.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private Long fromAccountId;
    private String fromAccountNumber;
    private Long toAccountId;
    private String toAccountNumber;
    private BigDecimal amount;
    private Transaction.TransactionType transactionType;
    private Transaction.TransactionStatus status;
    private String description;
    private String referenceNumber;
    private LocalDateTime createdAt;
}