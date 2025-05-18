package com.banking.card_service.repository;

import com.banking.card_service.entity.Card;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    @Query("SELECT c FROM Card c WHERE " +
            "(:cardAlias IS NULL OR c.cardAlias LIKE %:cardAlias%) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:pan IS NULL OR c.pan LIKE %:pan%)")
    Page<Card> searchCards(
            @Param("cardAlias") String cardAlias,
            @Param("type") com.banking.card_service.entity.CardType type,
            @Param("pan") String pan,
            Pageable pageable
    );
}
