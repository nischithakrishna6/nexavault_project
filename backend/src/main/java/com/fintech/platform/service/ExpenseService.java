package com.fintech.platform.service;

import com.fintech.platform.dto.CreateExpenseRequest;
import com.fintech.platform.dto.ExpenseDTO;
import com.fintech.platform.dto.ExpenseStatsDTO;
import com.fintech.platform.model.Expense;
import com.fintech.platform.model.User;
import com.fintech.platform.repository.ExpenseRepository;
import com.fintech.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Transactional
    public ExpenseDTO createExpense(Long userId, CreateExpenseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());

        Expense savedExpense = expenseRepository.save(expense);
        return convertToDTO(savedExpense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseDTO> getUserExpenses(Long userId) {
        List<Expense> expenses = expenseRepository.findByUserIdOrderByDateDesc(userId);
        return expenses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpenseStatsDTO getExpenseStats(Long userId, String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (period.toLowerCase()) {
            case "daily":
                startDate = endDate.minusDays(30); // Last 30 days
                break;
            case "weekly":
                startDate = endDate.minusWeeks(12); // Last 12 weeks
                break;
            case "monthly":
                startDate = endDate.minusMonths(12); // Last 12 months
                break;
            default:
                startDate = endDate.minusDays(30);
        }

        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(
                userId, startDate, endDate);

        ExpenseStatsDTO stats = new ExpenseStatsDTO();

        // Total expenses
        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalExpenses(total);

        // Category breakdown
        stats.setCategoryBreakdown(calculateCategoryBreakdown(expenses, total));

        // Time-based stats
        switch (period.toLowerCase()) {
            case "daily":
                stats.setDailyExpenses(calculateDailyExpenses(expenses, startDate, endDate));
                break;
            case "weekly":
                stats.setWeeklyExpenses(calculateWeeklyExpenses(expenses, startDate, endDate));
                break;
            case "monthly":
                stats.setMonthlyExpenses(calculateMonthlyExpenses(expenses, startDate, endDate));
                break;
        }

        return stats;
    }

    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this expense");
        }

        expenseRepository.delete(expense);
    }

    private List<ExpenseStatsDTO.CategoryExpenseDTO> calculateCategoryBreakdown(
            List<Expense> expenses, BigDecimal total) {

        Map<String, BigDecimal> categoryMap = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
                ));

        return categoryMap.entrySet().stream()
                .map(entry -> {
                    BigDecimal amount = entry.getValue();
                    Double percentage = total.compareTo(BigDecimal.ZERO) > 0
                            ? amount.divide(total, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;
                    return new ExpenseStatsDTO.CategoryExpenseDTO(
                            entry.getKey(), amount, percentage);
                })
                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                .collect(Collectors.toList());
    }

    private List<ExpenseStatsDTO.DailyExpenseDTO> calculateDailyExpenses(
            List<Expense> expenses, LocalDate startDate, LocalDate endDate) {

        Map<LocalDate, BigDecimal> dailyMap = new TreeMap<>();

        // Initialize all dates with zero
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            dailyMap.put(date, BigDecimal.ZERO);
            date = date.plusDays(1);
        }

        // Fill in actual expenses
        expenses.forEach(expense ->
                dailyMap.merge(expense.getDate(), expense.getAmount(), BigDecimal::add));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dailyMap.entrySet().stream()
                .map(entry -> new ExpenseStatsDTO.DailyExpenseDTO(
                        entry.getKey().format(formatter), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<ExpenseStatsDTO.WeeklyExpenseDTO> calculateWeeklyExpenses(
            List<Expense> expenses, LocalDate startDate, LocalDate endDate) {

        Map<String, BigDecimal> weeklyMap = new LinkedHashMap<>();

        LocalDate weekStart = startDate.with(TemporalAdjusters.previousOrSame(
                java.time.DayOfWeek.MONDAY));

        while (!weekStart.isAfter(endDate)) {
            LocalDate weekEnd = weekStart.plusDays(6);
            String weekLabel = "Week of " + weekStart.format(DateTimeFormatter.ofPattern("MMM dd"));
            weeklyMap.put(weekLabel, BigDecimal.ZERO);
            weekStart = weekStart.plusWeeks(1);
        }

        // Fill in actual expenses
        expenses.forEach(expense -> {
            LocalDate expenseWeekStart = expense.getDate()
                    .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            String weekLabel = "Week of " + expenseWeekStart.format(DateTimeFormatter.ofPattern("MMM dd"));
            weeklyMap.merge(weekLabel, expense.getAmount(), BigDecimal::add);
        });

        return weeklyMap.entrySet().stream()
                .map(entry -> new ExpenseStatsDTO.WeeklyExpenseDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<ExpenseStatsDTO.MonthlyExpenseDTO> calculateMonthlyExpenses(
            List<Expense> expenses, LocalDate startDate, LocalDate endDate) {

        Map<String, BigDecimal> monthlyMap = new LinkedHashMap<>();

        LocalDate monthStart = startDate.withDayOfMonth(1);
        while (!monthStart.isAfter(endDate)) {
            String monthLabel = monthStart.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            monthlyMap.put(monthLabel, BigDecimal.ZERO);
            monthStart = monthStart.plusMonths(1);
        }

        // Fill in actual expenses
        expenses.forEach(expense -> {
            String monthLabel = expense.getDate().format(DateTimeFormatter.ofPattern("MMM yyyy"));
            monthlyMap.merge(monthLabel, expense.getAmount(), BigDecimal::add);
        });

        return monthlyMap.entrySet().stream()
                .map(entry -> new ExpenseStatsDTO.MonthlyExpenseDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setUserId(expense.getUser().getId());
        dto.setCategory(expense.getCategory());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setDate(expense.getDate());
        dto.setCreatedAt(expense.getCreatedAt());
        return dto;
    }
}