package com.fintech.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseStatsDTO {
    private BigDecimal totalExpenses;
    private List<CategoryExpenseDTO> categoryBreakdown;
    private List<DailyExpenseDTO> dailyExpenses;
    private List<WeeklyExpenseDTO> weeklyExpenses;
    private List<MonthlyExpenseDTO> monthlyExpenses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryExpenseDTO {
        private String category;
        private BigDecimal amount;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyExpenseDTO {
        private String date;
        private BigDecimal amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyExpenseDTO {
        private String week;
        private BigDecimal amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyExpenseDTO {
        private String month;
        private BigDecimal amount;
    }
}