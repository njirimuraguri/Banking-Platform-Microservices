package com.banking.account_service.service;

import com.banking.account_service.client.CustomerClient;
import com.banking.account_service.dto.*;
import com.banking.account_service.entity.Account;
import com.banking.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service class responsible for handling account-related business logic.
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final CustomerClient customerClient;

    /**
     * Create a new account and return the response DTO.
     */
    public AccountResponse createAccount(AccountRequest request) {
        // Verify customer exists via Feign
        CustomerDto customer = customerClient.getCustomerById(request.getCustomerId());

        Account account = Account.builder()
                .iban(request.getIban())
                .bicSwift(request.getBicSwift())
                .customerId(customer.getCustomerId())
                .build();

        Account saved = repository.save(account);
        return toResponse(saved);
    }

    /**
     * Retrieve a single account by its ID.
     */
    public AccountResponse getAccount(UUID id) {
        Account account = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));

        return toResponse(account);
    }

    /**
     * Paginated search for accounts using optional filters.
     */
    public Page<AccountResponse> searchAccounts(String iban, String bicSwift, Pageable pageable) {
        return repository.searchAccounts(iban, bicSwift, pageable)
                .map(this::toResponse);
    }

    /**
     * Update IBAN and BIC Swift values for an existing account.
     */
    public AccountResponse updateAccount(UUID id, AccountUpdateRequest request) {
        Account account = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));

        account.setIban(request.getIban());
        account.setBicSwift(request.getBicSwift());

        account = repository.save(account);
        return toResponse(account);
    }

    /**
     * Delete an account by its ID.
     */
    public void deleteAccount(UUID id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Account not found");
        }
        repository.deleteById(id);
    }

    /**
     * Convert an Account entity to its corresponding DTO.
     */
    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .iban(account.getIban())
                .bicSwift(account.getBicSwift())
                .customerId(account.getCustomerId())
                .build();
    }
}
