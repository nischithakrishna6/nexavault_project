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
        System.out.println("===== TRANSFER MONEY DEBUG =====");
        System.out.println("From Account ID: " + request.getFromAccountId());
        System.out.println("To Account Number: " + request.getToAccountNumber());
        System.out.println("Amount: ₹" + request.getAmount());
        System.out.println("Description: " + request.getDescription());

        // STEP 1: Get source account
        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> {
                    System.err.println("✗ Source account not found: " + request.getFromAccountId());
                    return new RuntimeException("Source account not found");
                });

        System.out.println("✓ Found source account: " + fromAccount.getAccountNumber());
        System.out.println("  Balance: ₹" + fromAccount.getBalance());

        // STEP 2: Clean destination account number
        String destinationAccountNumber = request.getToAccountNumber().trim();
        System.out.println("Looking for destination account: '" + destinationAccountNumber + "'");

        List<Account> allAccounts = accountRepository.findAll();
        System.out.println("All accounts in database:");


        // STEP 3: Find destination account
        Account toAccount = accountRepository.findByAccountNumber(destinationAccountNumber)
                .orElseThrow(() -> {
                    System.err.println("✗ Destination account not found: '" + destinationAccountNumber + "'");
                    return new RuntimeException("Destination account not found. Please check the account number.");
                });

        System.out.println("✓ Found destination account: " + toAccount.getAccountNumber());
        System.out.println("  Current balance: ₹" + toAccount.getBalance());
        System.out.println("  Owner: " + toAccount.getUser().getEmail());

        // STEP 4: Cannot transfer to same account
        if (fromAccount.getId().equals(toAccount.getId())) {
            System.err.println("✗ Cannot transfer to same account");
            throw new RuntimeException("Cannot transfer money to the same account");
        }

        // STEP 5: Sufficient balance check
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            System.err.println("✗ Insufficient balance");
            System.err.println("  Required: ₹" + request.getAmount());
            System.err.println("  Available: ₹" + fromAccount.getBalance());
            throw new RuntimeException("Insufficient balance. Available: ₹" + fromAccount.getBalance());
        }

        // STEP 6: Amount must be positive
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("✗ Invalid amount: " + request.getAmount());
            throw new RuntimeException("Amount must be greater than zero");
        }

        // STEP 7: Only sender's account needs to be active
        // ✅ Receiver's isActive is NOT checked — a zero-balance or inactive
        //    account can still receive money (same as real banking behaviour)
        if (!fromAccount.getIsActive()) {
            System.err.println("✗ Source account is not active");
            throw new RuntimeException("Source account is not active");
        }

        System.out.println("✓ All validations passed. Processing transfer...");

        // STEP 8: Create transaction
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setDescription(request.getDescription() != null
                ? request.getDescription() : "Money Transfer");
        transaction.setReferenceNumber(generateReferenceNumber());

        // STEP 9: Update balances
        BigDecimal fromOldBalance = fromAccount.getBalance();
        BigDecimal toOldBalance = toAccount.getBalance();

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        System.out.println("✓ Balances updated:");
        System.out.println("  From: ₹" + fromOldBalance + " → ₹" + fromAccount.getBalance());
        System.out.println("  To: ₹" + toOldBalance + " → ₹" + toAccount.getBalance());

        // STEP 10: Mark transaction complete
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        Transaction savedTransaction = transactionRepository.save(transaction);

        System.out.println("✓ Transaction completed: " + savedTransaction.getReferenceNumber());
        System.out.println("✓ Transaction ID: " + savedTransaction.getId());
        System.out.println("✓ From Account ID: " + savedTransaction.getFromAccount().getId());
        System.out.println("✓ To Account ID: " + savedTransaction.getToAccount().getId());
        System.out.println("✓ Amount: " + savedTransaction.getAmount());
        System.out.println("✓ Status: " + savedTransaction.getStatus());

        // STEP 11: Real-time notifications
        try {
            TransactionDTO transactionDTO = convertToDTO(savedTransaction);

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + fromAccount.getUser().getId(),
                    "Transfer completed: ₹" + request.getAmount() +
                            " sent to " + toAccount.getAccountNumber()
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + toAccount.getUser().getId(),
                    "Money received: ₹" + request.getAmount() +
                            " from " + fromAccount.getAccountNumber()
            );

            System.out.println("✓ Notifications sent");
            System.out.println("================================");

            return transactionDTO;

        } catch (Exception e) {
            System.err.println("✗ Failed to send notifications: " + e.getMessage());
            System.out.println("================================");
            return convertToDTO(savedTransaction);
        }
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
            dto.setFromAccountHolderName(transaction.getFromAccount().getAccountHolderName());
            dto.setFromBankCode(transaction.getFromAccount().getBankCode());
            dto.setFromBankName(transaction.getFromAccount().getBankName());
        }

        if (transaction.getToAccount() != null) {
            dto.setToAccountId(transaction.getToAccount().getId());
            dto.setToAccountNumber(transaction.getToAccount().getAccountNumber());
            dto.setToAccountHolderName(transaction.getToAccount().getAccountHolderName());
            dto.setToBankCode(transaction.getToAccount().getBankCode());
            dto.setToBankName(transaction.getToAccount().getBankName());
        }

        return dto;
    }


    private String generateReferenceNumber() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}