package com.fintech.platform.controller;

import com.fintech.platform.dto.ApiResponse;
import com.fintech.platform.dto.CreateExpenseRequest;
import com.fintech.platform.dto.ExpenseDTO;
import com.fintech.platform.dto.ExpenseStatsDTO;
import com.fintech.platform.security.UserDetailsImpl;
import com.fintech.platform.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseDTO>> createExpense(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CreateExpenseRequest request) {
        try {
            ExpenseDTO expense = expenseService.createExpense(userDetails.getId(), request);
            return ResponseEntity.ok(ApiResponse.success("Expense created successfully", expense));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseDTO>>> getUserExpenses(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            List<ExpenseDTO> expenses = expenseService.getUserExpenses(userDetails.getId());
            return ResponseEntity.ok(ApiResponse.success("Expenses retrieved", expenses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ExpenseStatsDTO>> getExpenseStats(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "daily") String period) {
        try {
            ExpenseStatsDTO stats = expenseService.getExpenseStats(userDetails.getId(), period);
            return ResponseEntity.ok(ApiResponse.success("Stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        try {
            expenseService.deleteExpense(id, userDetails.getId());
            return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}