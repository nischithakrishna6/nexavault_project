package com.fintech.platform.controller;

import com.fintech.platform.dto.AccountDTO;
import com.fintech.platform.dto.ApiResponse;
import com.fintech.platform.dto.LinkAccountRequest;
import com.fintech.platform.security.UserDetailsImpl;
import com.fintech.platform.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/link")
    public ResponseEntity<ApiResponse<AccountDTO>> linkAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody LinkAccountRequest request) {
        try {
            AccountDTO account = accountService.linkExistingAccount(userDetails.getId(), request);
            return ResponseEntity.ok(ApiResponse.success(
                    "Account linked successfully. Verification pending.", account));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getUserAccounts(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            List<AccountDTO> accounts = accountService.getUserAccounts(userDetails.getId());
            return ResponseEntity.ok(ApiResponse.success("Accounts retrieved", accounts));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountById(@PathVariable Long id) {
        try {
            AccountDTO account = accountService.getAccountById(id);
            return ResponseEntity.ok(ApiResponse.success("Account retrieved", account));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Account not found"));
        }
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getAccountBalance(@PathVariable Long id) {
        try {
            BigDecimal balance = accountService.getAccountBalance(id);
            return ResponseEntity.ok(ApiResponse.success("Balance retrieved", balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Account not found"));
        }
    }
}