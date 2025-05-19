package com.banking.account_service;

import com.banking.account_service.dto.*;
import com.banking.account_service.entity.Account;
import com.banking.account_service.repository.AccountRepository;
import com.banking.account_service.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccount_success() {
        AccountRequest request = new AccountRequest("KE123", "JUBANK", UUID.randomUUID());

        Account saved = Account.builder()
                .accountId(UUID.randomUUID())
                .iban("KE123")
                .bicSwift("JUBANK")
                .customerId(request.getCustomerId())
                .build();

        when(accountRepository.save(any())).thenReturn(saved);

        AccountResponse response = accountService.createAccount(request);

        assertNotNull(response);
        assertEquals("KE123", response.getIban());
        verify(accountRepository).save(any());
    }

    @Test
    void testGetAccount_byId_success() {
        UUID id = UUID.randomUUID();
        Account account = Account.builder().accountId(id).iban("KE456").build();
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        AccountResponse result = accountService.getAccount(id);

        assertNotNull(result);
        assertEquals("KE456", result.getIban());
    }

    @Test
    void testUpdate_success() {
        UUID id = UUID.randomUUID();
        Account existing = Account.builder().accountId(id).iban("OLD").bicSwift("OLD").customerId(UUID.randomUUID()).build();

        when(accountRepository.findById(id)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any())).thenReturn(existing);

        AccountUpdateRequest updateRequest = new AccountUpdateRequest();

        AccountResponse result = accountService.updateAccount(id, updateRequest);

        assertEquals("NEW_IBAN", result.getIban());
        assertEquals("NEW_BIC", result.getBicSwift());
    }

    @Test
    void testDelete_success() {
        UUID id = UUID.randomUUID();
        when(accountRepository.existsById(id)).thenReturn(true);

        accountService.deleteAccount(id);

        verify(accountRepository).deleteById(id);
    }

    @Test
    void testDelete_nonexistentAccount_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(accountRepository.existsById(id)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> accountService.deleteAccount(id));
    }
}
