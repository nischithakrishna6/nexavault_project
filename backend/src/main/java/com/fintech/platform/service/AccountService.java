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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public AccountDTO linkExistingAccount(Long userId, LinkAccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if account already linked
        Optional<Account> existingLink = accountRepository.findByAccountNumber(request.getExistingAccountNumber());
        if (existingLink.isPresent()) {
            throw new RuntimeException("This account is already linked to another user");
        }

        // Extract bank code from IFSC
        String bankCodeFromIFSC = request.getIfscCode().substring(0, 4);
        if (!bankCodeFromIFSC.equals(request.getBankCode())) {
            throw new RuntimeException("Bank code doesn't match IFSC code");
        }

        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(request.getExistingAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBankName(request.getBankName());
        account.setBankCode(request.getBankCode());
        account.setBranchName(request.getBranchName());
        account.setIfscCode(request.getIfscCode().toUpperCase());
        account.setAccountHolderName(request.getAccountHolderName());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("INR");
        account.setIsActive(false); // Pending verification
        account.setIsVerified(false); // Not verified yet

        Account savedAccount = accountRepository.save(account);
        return convertToDTO(savedAccount);
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

    private AccountDTO convertToDTO(Account account) {
        return modelMapper.map(account, AccountDTO.class);
    }
}