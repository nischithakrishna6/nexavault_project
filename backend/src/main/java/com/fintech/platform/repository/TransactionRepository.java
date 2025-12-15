package com.fintech.platform.repository;

import com.fintech.platform.model.Account;
import com.fintech.platform.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountOrToAccountOrderByCreatedAtDesc(
            Account fromAccount, Account toAccount);

    List<Transaction> findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(
            Long fromAccountId, Long toAccountId);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) " +
            "AND t.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findAccountTransactionsBetweenDates(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    @Query("SELECT t FROM Transaction t WHERE " +
            "t.fromAccount.user.id = :userId OR t.toAccount.user.id = :userId " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findUserTransactions(@Param("userId") Long userId);
}
