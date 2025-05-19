package com.banking.card_service.repository;

import com.banking.card_service.entity.Card;
import com.banking.card_service.entity.CardType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    @Query("SELECT c FROM Card c WHERE " +
            "(:cardAlias IS NULL OR LOWER(c.cardAlias) LIKE :cardAlias) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:pan IS NULL OR c.pan LIKE :pan)")
    Page<Card> searchCards(
            @Param("cardAlias") String cardAlias,
            @Param("type") CardType type,
            @Param("pan") String pan,
            Pageable pageable
    );


    boolean existsByAccountIdAndType(UUID accountId, CardType type);

    int countByAccountId(UUID accountId);
}
