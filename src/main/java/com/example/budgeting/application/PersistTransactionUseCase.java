package com.example.budgeting.application;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import com.example.budgeting.application.input.PersistTransactionInput;
import com.example.budgeting.application.output.TransactionOutput;
import com.example.budgeting.domain.Transaction;
import com.example.budgeting.domain.TransactionRepository;

@Service
public class PersistTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public PersistTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "persist-transaction", description = "Persiste uma nova transação financeira")
    public TransactionOutput execute(PersistTransactionInput input) {
        if (input.amount() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
        if (input.description() == null || input.description().isBlank()) {
            throw new IllegalArgumentException("A descrição não pode ser vazia");
        }
        if (input.category() == null) {
            throw new IllegalArgumentException("A categoria não pode ser nula");
        }
        var transaction = transactionRepository.save(
                new Transaction(input.description(), input.amount(), input.category()));

        return TransactionOutput.from(transaction);
    }
}
