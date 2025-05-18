package com.banking.account_service.controller;

import com.banking.account_service.dto.*;
import com.banking.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST controller for managing account resources.
 * Supports creation, retrieval, update, deletion, and search.
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    /**
     * Create a new account.
     */
    @PostMapping
    public ResponseEntity<StandardResponse<AccountResponse>> createAccount(
            @RequestBody AccountRequest request
    ) {
        AccountResponse created = service.createAccount(request);

        return ResponseEntity.ok(StandardResponse.<AccountResponse>builder()
                .message("Account created successfully")
                .timestamp(LocalDateTime.now())
                .data(created)
                .build());
    }

    /**
     * Get account details by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<AccountResponse>> getAccount(@PathVariable UUID id) {
        AccountResponse account = service.getAccount(id);

        return ResponseEntity.ok(StandardResponse.<AccountResponse>builder()
                .message("Account retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(account)
                .build());
    }

    /**
     * Update an existing account's IBAN and BIC Swift.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<AccountResponse>> updateAccount(
            @PathVariable UUID id,
            @RequestBody AccountUpdateRequest request
    ) {
        AccountResponse updated = service.updateAccount(id, request);

        return ResponseEntity.ok(StandardResponse.<AccountResponse>builder()
                .message("Account updated successfully")
                .timestamp(LocalDateTime.now())
                .data(updated)
                .build());
    }

    /**
     * Delete an account by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteAccount(@PathVariable UUID id) {
        service.deleteAccount(id);

        return ResponseEntity.ok(StandardResponse.<Void>builder()
                .message("Account deleted successfully")
                .timestamp(LocalDateTime.now())
                .data(null)
                .build());
    }

    /**
     * Search accounts using optional IBAN and BIC Swift filters.
     */
    @GetMapping
    public ResponseEntity<StandardResponse<Page<AccountResponse>>> searchAccounts(
            @RequestParam(required = false) String iban,
            @RequestParam(required = false) String bicSwift,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Page<AccountResponse> accounts = service.searchAccounts(iban, bicSwift, pageable);

        return ResponseEntity.ok(StandardResponse.<Page<AccountResponse>>builder()
                .message("Accounts fetched successfully")
                .timestamp(LocalDateTime.now())
                .data(accounts)
                .build());
    }
}
