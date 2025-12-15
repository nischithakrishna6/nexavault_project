
// ============================================
// FILE 3: src/main/java/com/fintech/platform/controller/TransactionController.java
// ============================================
package com.fintech.platform.controller;

import com.fintech.platform.dto.ApiResponse;
import com.fintech.platform.dto.TransactionDTO;
import com.fintech.platform.dto.TransferRequest;
import com.fintech.platform.security.UserDetailsImpl;
import com.fintech.platform.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionDTO>> transferMoney(
            @Valid @RequestBody TransferRequest request) {
        try {
            TransactionDTO transaction = transactionService.transferMoney(request);
            return ResponseEntity.ok(ApiResponse.success("Transfer successful", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getUserTransactions(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            List<TransactionDTO> transactions = transactionService.getUserTransactions(userDetails.getId());
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionById(@PathVariable Long id) {
        try {
            TransactionDTO transaction = transactionService.getTransactionById(id);
            return ResponseEntity.ok(ApiResponse.success("Transaction retrieved", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Transaction not found"));
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getAccountTransactions(
            @PathVariable Long accountId) {
        try {
            List<TransactionDTO> transactions = transactionService.getAccountTransactions(accountId);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
