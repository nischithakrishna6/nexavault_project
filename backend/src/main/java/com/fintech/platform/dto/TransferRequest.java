package com.fintech.platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull(message = "From account ID is required")
    private Long fromAccountId;

    @NotBlank(message = "To account number is required")
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;
}
