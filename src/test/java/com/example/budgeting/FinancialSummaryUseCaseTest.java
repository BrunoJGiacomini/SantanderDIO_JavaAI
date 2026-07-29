package com.example.budgeting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.budgeting.application.FinancialSummaryUseCase;
import com.example.budgeting.domain.Category;
import com.example.budgeting.domain.Transaction;
import com.example.budgeting.domain.TransactionRepository;

class FinancialSummaryUseCaseTest {

        @Test
        void shouldGenerateFinancialSummary() {

                // Arrange
                var repository = mock(TransactionRepository.class);

                var transaction1 = new Transaction(
                                "Mercado",
                                10000,
                                Category.GROCERIES);

                var transaction2 = new Transaction(
                                "Combustível",
                                25000,
                                Category.AUTO);

                var transaction3 = new Transaction(
                                "Farmácia",
                                5000,
                                Category.PHARMA);

                when(repository.findAll())
                                .thenReturn(List.of(
                                                transaction1,
                                                transaction2,
                                                transaction3));

                var useCase = new FinancialSummaryUseCase(repository);

                // Act
                var result = useCase.execute();

                // Assert
                assertThat(result.transactionCount()).isEqualTo(3);

                assertThat(result.totalAmount())
                                .isEqualByComparingTo("400.00");

                assertThat(result.averageAmount())
                                .isEqualByComparingTo("133.33");

                assertThat(result.biggestExpenseDescription())
                                .isEqualTo("Combustível");

                assertThat(result.biggestExpenseAmount())
                                .isEqualByComparingTo("250.00");
        }

        @Test
        void shouldReturnEmptySummary_whenThereAreNoTransactions() {

                // Arrange
                var repository = mock(TransactionRepository.class);

                when(repository.findAll())
                                .thenReturn(List.of());

                var useCase = new FinancialSummaryUseCase(repository);

                // Act
                var result = useCase.execute();

                // Debug
                System.out.println("RESULTADO DO TESTE:");
                System.out.println("transactionCount = " + result.transactionCount());
                System.out.println("totalAmount = " + result.totalAmount());
                System.out.println("averageAmount = " + result.averageAmount());
                System.out.println("biggestExpenseDescription = " + result.biggestExpenseDescription());
                System.out.println("biggestExpenseAmount = " + result.biggestExpenseAmount());

                // Assert
                assertThat(result.transactionCount()).isZero();
                assertThat(result.totalAmount()).isZero();
                assertThat(result.averageAmount()).isZero();
                assertThat(result.biggestExpenseDescription()).isNull();
                assertThat(result.biggestExpenseAmount()).isZero();
        }

        @Test
        void shouldRoundAverageAmountToTwoDecimalPlaces() {

                // Arrange
                var repository = mock(TransactionRepository.class);

                var transaction1 = new Transaction(
                                "Mercado",
                                1000,
                                Category.GROCERIES);

                var transaction2 = new Transaction(
                                "Farmácia",
                                1000,
                                Category.PHARMA);

                var transaction3 = new Transaction(
                                "Combustível",
                                1100,
                                Category.AUTO);

                when(repository.findAll())
                                .thenReturn(List.of(
                                                transaction1,
                                                transaction2,
                                                transaction3));

                var useCase = new FinancialSummaryUseCase(repository);

                // Act
                var result = useCase.execute();

                // Assert
                assertThat(result.averageAmount())
                                .isEqualByComparingTo("10.33");
        }
}
