

// ============================================
// FILE 3: src/main/java/com/fintech/platform/service/TransactionService.java
// ============================================
package com.fintech.platform.service;

import com.fintech.platform.dto.TransactionDTO;
import com.fintech.platform.dto.TransferRequest;
import com.fintech.platform.model.Account;
import com.fintech.platform.model.Transaction;
import com.fintech.platform.repository.AccountRepository;
import com.fintech.platform.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public TransactionDTO transferMoney(TransferRequest request) {
        // Get source account
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        // Get destination account
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // Validate sufficient balance
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Validate amount is positive
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setDescription(request.getDescription());
        transaction.setReferenceNumber(generateReferenceNumber());

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Complete transaction
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Send real-time notification
        TransactionDTO transactionDTO = convertToDTO(savedTransaction);
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + fromAccount.getUser().getId(),
                "Transaction completed: $" + request.getAmount()
        );
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + toAccount.getUser().getId(),
                "Money received: $" + request.getAmount()
        );

        return transactionDTO;
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getAccountTransactions(Long accountId) {
        List<Transaction> transactions = transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(accountId, accountId);
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getUserTransactions(Long userId) {
        List<Transaction> transactions = transactionRepository.findUserTransactions(userId);
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return convertToDTO(transaction);
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = modelMapper.map(transaction, TransactionDTO.class);
        if (transaction.getFromAccount() != null) {
            dto.setFromAccountId(transaction.getFromAccount().getId());
            dto.setFromAccountNumber(transaction.getFromAccount().getAccountNumber());
        }
        if (transaction.getToAccount() != null) {
            dto.setToAccountId(transaction.getToAccount().getId());
            dto.setToAccountNumber(transaction.getToAccount().getAccountNumber());
        }
        return dto;
    }

    private String generateReferenceNumber() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
//
// These services contain the business logic:
// - AuthService: Handles registration, login, token refresh
// - AccountService: Manages user accounts
// - TransactionService: Handles money transfers and notifications
//
