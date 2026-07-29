package com.example.budgeting.application.output;

import java.math.BigDecimal;

public record FinancialSummaryOutput(
        int transactionCount,
        BigDecimal totalAmount,
        BigDecimal averageAmount,
        String biggestExpenseDescription,
        BigDecimal biggestExpenseAmount
) {
}
