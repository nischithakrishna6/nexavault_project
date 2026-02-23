package com.fintech.platform.controller;

import com.fintech.platform.dto.TransactionDTO;
import com.fintech.platform.dto.TransferRequest;
import com.fintech.platform.model.User;
import com.fintech.platform.repository.UserRepository;
import com.fintech.platform.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;
    // ✅ REMOVED: private final InvoiceService invoiceService;

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferMoney(@RequestBody TransferRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("Transfer request received"); // ✅ Debug log
            TransactionDTO transaction = transactionService.transferMoney(request);
            response.put("success", true);
            response.put("message", "Transfer successful");
            response.put("transaction", transaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Transfer failed: " + e.getMessage()); // ✅ Debug log
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(
            Authentication authentication) {
        try {
            System.out.println("Getting transactions for: " + authentication.getName());

            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("User ID: " + user.getId());

            List<TransactionDTO> transactions = transactionService
                    .getUserTransactions(user.getId());

            System.out.println("Found " + transactions.size() + " transactions");

            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long transactionId) {
        try {
            TransactionDTO transaction = transactionService.getTransactionById(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getAccountTransactions(@PathVariable Long accountId) {
        try {
            List<TransactionDTO> transactions = transactionService.getAccountTransactions(accountId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}