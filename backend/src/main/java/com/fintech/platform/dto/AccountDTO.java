package com.fintech.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private String accountType;
    private String bankName;              // ✅ ADD
    private String bankCode;              // ✅ ADD
    private String branchName;            // ✅ ADD
    private String ifscCode;              // ✅ ADD
    private String accountHolderName;     // ✅ ADD
    private BigDecimal balance;
    private String currency;
    private Boolean isActive;
    private LocalDateTime createdAt;
}