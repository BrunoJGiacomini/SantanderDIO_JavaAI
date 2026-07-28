package com.example.budgeting.infrastructure.persistance.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.budgeting.domain.Category;
import com.example.budgeting.infrastructure.persistance.entity.TransactionEntity;

import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {
    List<TransactionEntity> findAllByCategory(Category category);
}