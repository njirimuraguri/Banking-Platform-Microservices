package com.banking.account_service.service;

import com.banking.account_service.client.CardClient;
import com.banking.account_service.client.CustomerClient;
import com.banking.account_service.dto.*;
import com.banking.account_service.entity.Account;
import com.banking.account_service.repository.AccountRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling account-related business logic.
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final CustomerClient customerClient;
    private final CardClient cardClient;

    /**
     * Create a new account and return the response DTO.
     */
    public AccountResponse createAccount(AccountRequest request) {
        // Validate customer exists
        try {
            StandardResponse<CustomerDto> response = customerClient.getCustomerById(request.getCustomerId());
            CustomerDto customer = response.getData();
            // continue with validation
        } catch (FeignException.NotFound ex) {
            throw new IllegalArgumentException("Customer with ID " + request.getCustomerId() + " not found");
        }

        Account account = Account.builder()
                .iban(request.getIban())
                .bicSwift(request.getBicSwift())
                .customerId(request.getCustomerId())
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


    public Page<AccountResponse> searchAccounts(String iban, String bicSwift, String cardAlias, Pageable pageable) {
        if (iban != null && iban.isBlank()) {

            iban = null;

        }

        if (cardAlias != null && !cardAlias.isBlank()) {
            StandardResponse<Page<CardPublicDto>> response = cardClient.getCardsByAlias(cardAlias);
            List<CardPublicDto> cards = response.getData().getContent();

            List<UUID> accountIds = cards.stream()
                    .map(CardPublicDto::getAccountId)
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println("Account IDs found from card-service (cardAlias=" + cardAlias + "): " + accountIds);

            if (accountIds.isEmpty()) {
                return Page.empty();
            }

            return repository.searchWithCardAlias(iban, bicSwift, accountIds, pageable)
                    .map(this::toResponse);
        }

        return repository.searchWithoutCardAlias(iban, bicSwift, pageable)
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
