package com.fintech.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {
    private Long id;
    private Long userId;
    private String category;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private LocalDateTime createdAt;
}