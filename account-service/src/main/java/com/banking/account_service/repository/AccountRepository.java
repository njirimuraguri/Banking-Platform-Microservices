package com.banking.account_service.repository;

import com.banking.account_service.entity.Account;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("SELECT a FROM Account a " +
            "WHERE (:iban IS NULL OR a.iban LIKE %:iban%) " +
            "AND (:bicSwift IS NULL OR a.bicSwift LIKE %:bicSwift%)")
    Page<Account> searchAccounts(
            @Param("iban") String iban,
            @Param("bicSwift") String bicSwift,
            Pageable pageable
    );
}
