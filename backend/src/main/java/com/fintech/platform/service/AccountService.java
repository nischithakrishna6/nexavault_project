package com.fintech.platform.service;

import com.fintech.platform.dto.AccountDTO;
import com.fintech.platform.dto.LinkAccountRequest;
import com.fintech.platform.model.Account;
import com.fintech.platform.model.User;
import com.fintech.platform.repository.AccountRepository;
import com.fintech.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private static final boolean DEMO_MODE = true;
    private static final BigDecimal DEMO_BALANCE = new BigDecimal("10000.00");

    @Transactional
    public AccountDTO linkExistingAccount(Long userId, LinkAccountRequest request) {
        System.out.println("===== LINKING ACCOUNT =====");
        System.out.println("User ID: " + userId);
        System.out.println("Account Number: " + request.getExistingAccountNumber());
        System.out.println("Bank Code: " + request.getBankCode());
        System.out.println("Bank Name: " + request.getBankName());
        System.out.println("IFSC Code: " + request.getIfscCode());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Account> existingLink = accountRepository.findByAccountNumber(request.getExistingAccountNumber());
        if (existingLink.isPresent()) {
            throw new RuntimeException("This account is already linked to another user");
        }

        String bankCodeFromIFSC = request.getIfscCode().substring(0, 4).toUpperCase();
        String requestBankCode = request.getBankCode().toUpperCase();

        if (!bankCodeFromIFSC.equals(requestBankCode)) {
            throw new RuntimeException(
                    String.format("Bank code mismatch. Expected %s from IFSC but got %s",
                            bankCodeFromIFSC, requestBankCode)
            );
        }

        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(request.getExistingAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBankName(request.getBankName());
        account.setBankCode(requestBankCode);
        account.setBranchName(request.getBranchName());
        account.setIfscCode(request.getIfscCode().toUpperCase());
        account.setAccountHolderName(request.getAccountHolderName());

        if (DEMO_MODE) {
            BigDecimal randomBalance = generateRandomBalance();
            account.setBalance(randomBalance);
            System.out.println("✓ Demo Mode: Initial balance set to ₹" + randomBalance);
        } else {
            account.setBalance(BigDecimal.ZERO);
            System.out.println("✓ Production Mode: Balance set to zero");
        }

        account.setCurrency("INR");

        if (DEMO_MODE) {
            account.setIsActive(true);
            account.setIsVerified(true);
            System.out.println("✓ Demo Mode: Account activated and verified");
        } else {
            account.setIsActive(false);
            account.setIsVerified(false);
            System.out.println("✓ Production Mode: Account pending verification");
        }

        Account savedAccount = accountRepository.save(account);
        System.out.println("✓ Account saved successfully with ID: " + savedAccount.getId());
        System.out.println("============================");

        return convertToDTO(savedAccount);
    }

    private BigDecimal generateRandomBalance() {
        Random random = new Random();
        int minBalance = 5000;
        int maxBalance = 50000;
        int randomAmount = random.nextInt(maxBalance - minBalance + 1) + minBalance;
        return new BigDecimal(randomAmount);
    }

    @Transactional
    public AccountDTO addBalance(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        Account savedAccount = accountRepository.save(account);
        System.out.println("✓ Added ₹" + amount + " to account. New balance: ₹" + newBalance);

        return convertToDTO(savedAccount);
    }

    @Transactional
    public void deductBalance(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance. Available: ₹" + account.getBalance());
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);

        accountRepository.save(account);
        System.out.println("✓ Deducted ₹" + amount + " from account. New balance: ₹" + newBalance);
    }

    @Transactional
    public void creditBalance(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        accountRepository.save(account);
        System.out.println("✓ Credited ₹" + amount + " to account. New balance: ₹" + newBalance);
    }

    @Transactional(readOnly = true)
    public List<AccountDTO> getUserAccounts(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return convertToDTO(account);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAccountBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return account.getBalance();
    }

    @Transactional(readOnly = true)
    public boolean isAccountOwnedByUser(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return account.getUser().getId().equals(userId);
    }

    // ✅ UPDATED: Explicit mapping instead of ModelMapper
    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();

        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBankName(account.getBankName());              // ✅ Explicitly set
        dto.setBankCode(account.getBankCode());              // ✅ Explicitly set
        dto.setBranchName(account.getBranchName());
        dto.setIfscCode(account.getIfscCode());
        dto.setAccountHolderName(account.getAccountHolderName());
        dto.setBalance(account.getBalance());
        dto.setCurrency(account.getCurrency());
        dto.setIsActive(account.getIsActive());
        dto.setCreatedAt(account.getCreatedAt());

        System.out.println("✓ DTO Created - Bank Name: " + dto.getBankName() +
                ", Bank Code: " + dto.getBankCode()); // Debug log

        return dto;
    }
}