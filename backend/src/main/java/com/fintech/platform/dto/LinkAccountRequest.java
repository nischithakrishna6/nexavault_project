package com.fintech.platform.dto;

import com.fintech.platform.model.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class LinkAccountRequest {

    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Branch name is required")
    private String branchName;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
    private String ifscCode;

    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;

    @NotBlank(message = "Existing account number is required")
    @Pattern(regexp = "^[0-9]{9,18}$", message = "Invalid account number")
    private String existingAccountNumber;

    @NotBlank(message = "Bank code is required")
    @Size(min = 4, max = 4, message = "Bank code must be 4 characters")
    private String bankCode;
}
