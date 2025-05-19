package com.banking.account_service;

import com.banking.account_service.entity.Account;
import com.banking.account_service.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Test
    void testSearchByCardAlias() {
        // Setup
        Account a1 = Account.builder()
                .iban("KE001")
                .bicSwift("JUB1")
                .customerId(UUID.randomUUID())
                .build();
        repository.save(a1);

        // Action
        Page<Account> result = repository.searchWithCardAlias("KE001", "JUB1", List.of(a1.getAccountId()), PageRequest.of(0, 10));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIban()).isEqualTo("KE001");
    }
}
