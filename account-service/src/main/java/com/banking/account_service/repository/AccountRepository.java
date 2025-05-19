package com.banking.account_service.repository;

import com.banking.account_service.entity.Account;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query("SELECT a FROM Account a WHERE " +
            "(:iban IS NULL OR a.iban = :iban) AND " +
            "(:bicSwift IS NULL OR a.bicSwift = :bicSwift)")
    Page<Account> searchWithoutCardAlias(
            @Param("iban") String iban,
            @Param("bicSwift") String bicSwift,
            Pageable pageable
    );

    @Query("SELECT a FROM Account a WHERE " +
            "(:iban IS NULL OR a.iban = :iban) AND " +
            "(:bicSwift IS NULL OR a.bicSwift = :bicSwift) AND " +
            "a.accountId IN :accountIds")
    Page<Account> searchWithCardAlias(
            @Param("iban") String iban,
            @Param("bicSwift") String bicSwift,
            @Param("accountIds") List<UUID> accountIds,
            Pageable pageable
    );



}
