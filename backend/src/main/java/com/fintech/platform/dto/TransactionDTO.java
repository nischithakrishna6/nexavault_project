package com.fintech.platform.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private Long fromAccountId;
    private String fromAccountNumber;
    private String toAccountHolderName;
    private String fromAccountHolderName;
    private Long toAccountId;
    private String toAccountNumber;
    private BigDecimal amount;
    private String transactionType;
    private String toBankName;
    private String fromBankName;
    private String status;
    private String description;
    private String referenceNumber;
    private LocalDateTime createdAt;
    private String toBankCode;
    private String fromBankCode;

    // REMOVE these if you added them:
    // private String direction;
    // private String otherPartyName;
    // private String otherPartyAccountNumber;
    // private String fromAccountHolderName;
    // private String toAccountHolderName;
}