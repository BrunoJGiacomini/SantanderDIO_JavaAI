package com.example.budgeting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.example.budgeting.application.PersistTransactionUseCase;
import com.example.budgeting.application.input.PersistTransactionInput;
import com.example.budgeting.domain.Category;
import com.example.budgeting.domain.Transaction;
import com.example.budgeting.domain.TransactionRepository;

class PersistTransactionUseCaseTest {

    @Test
    void shouldThrowException_whenAmountIsZero() {
        var repository = mock(TransactionRepository.class);

        var useCase = new PersistTransactionUseCase(repository);

        var input = new PersistTransactionInput(
                "Mercado",
                0,
                Category.GROCERIES);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O valor deve ser maior que zero");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenAmountIsNegative() {
        var repository = mock(TransactionRepository.class);

        var useCase = new PersistTransactionUseCase(repository);

        var input = new PersistTransactionInput(
                "Mercado",
                -100,
                Category.GROCERIES);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O valor deve ser maior que zero");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenDescriptionIsBlank() {
        var repository = mock(TransactionRepository.class);

        var useCase = new PersistTransactionUseCase(repository);

        var input = new PersistTransactionInput(
                "   ",
                10000,
                Category.GROCERIES);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A descrição não pode ser vazia");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenCategoryIsNull() {
        var repository = mock(TransactionRepository.class);

        var useCase = new PersistTransactionUseCase(repository);

        var input = new PersistTransactionInput(
                "Mercado",
                10000,
                null);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A categoria não pode ser nula");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldPersistTransaction_whenInputIsValid() {
        var repository = mock(TransactionRepository.class);

        var useCase = new PersistTransactionUseCase(repository);

        var input = new PersistTransactionInput(
                "Mercado",
                10000,
                Category.GROCERIES);

        var transaction = new Transaction(
                "Mercado",
                10000,
                Category.GROCERIES);

        when(repository.save(any(Transaction.class)))
                .thenReturn(transaction);

        var result = useCase.execute(input);

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo("Mercado");
        assertThat(result.category()).isEqualTo("GROCERIES");
        assertThat(result.value()).isEqualTo(100.00);

        verify(repository).save(any(Transaction.class));
    }
}
