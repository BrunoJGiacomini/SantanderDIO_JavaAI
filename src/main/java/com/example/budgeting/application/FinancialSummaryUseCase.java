package com.example.budgeting.application;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import com.example.budgeting.application.output.FinancialSummaryOutput;
import com.example.budgeting.domain.TransactionRepository;

@Service
public class FinancialSummaryUseCase {

    private final TransactionRepository transactionRepository;

    public FinancialSummaryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(
        name = "financial-summary",
        description = "Gera um resumo financeiro das transações cadastradas, informando a quantidade de transações, o valor total, a média dos valores, a maior despesa e sua descrição."
    )
    public FinancialSummaryOutput execute() {
        var transactions = transactionRepository.findAll();

        if (transactions.isEmpty()) {
            return new FinancialSummaryOutput(
                    0,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    null,
                    BigDecimal.ZERO);
        }

        int transactionCount = transactions.size();

        long totalAmountInCents = transactions.stream()
                .mapToLong(transaction -> transaction.getAmount())
                .sum();

        BigDecimal averageAmount = BigDecimal.valueOf(totalAmountInCents)
                .movePointLeft(2)
                .divide(
                        BigDecimal.valueOf(transactionCount),
                        2,
                        RoundingMode.HALF_UP);

        var biggestExpense = transactions.stream()
                .max((t1, t2) -> Long.compare(t1.getAmount(), t2.getAmount()))
                .orElseThrow();

        BigDecimal totalAmountInReais = BigDecimal.valueOf(totalAmountInCents)
                .movePointLeft(2)
                .setScale(2, RoundingMode.HALF_UP);

        return new FinancialSummaryOutput(
                transactionCount,
                totalAmountInReais,
                averageAmount,
                biggestExpense.getDescription(),
                BigDecimal.valueOf(biggestExpense.getAmount())
                        .movePointLeft(2)
                        .setScale(2, RoundingMode.HALF_UP));
    }
}
