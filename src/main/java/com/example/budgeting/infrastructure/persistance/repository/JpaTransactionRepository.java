package com.example.budgeting.infrastructure.persistance.repository;

import org.springframework.stereotype.Repository;

import com.example.budgeting.domain.Category;
import com.example.budgeting.domain.Transaction;
import com.example.budgeting.domain.TransactionRepository;
import com.example.budgeting.infrastructure.persistance.entity.TransactionEntity;

import java.util.List;

@Repository
public class JpaTransactionRepository implements TransactionRepository {
    private final TransactionEntityRepository transactionEntityRepository;

    public JpaTransactionRepository(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.from(transaction);
        return transactionEntityRepository.save(entity).toDomain();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return transactionEntityRepository.findAllByCategory(category)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
