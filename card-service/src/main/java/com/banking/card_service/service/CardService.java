package com.banking.card_service.service;

import com.banking.card_service.client.AccountClient;
import com.banking.card_service.dto.*;
import com.banking.card_service.entity.*;
import com.banking.card_service.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository repository;
    private final AccountClient accountClient;

    public CardSensitiveResponse create(CardRequest request) {
        // Validate that accountId exists via OpenFeign
        try {
            accountClient.getAccountById(request.getAccountId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Account with ID " + request.getAccountId() + " does not exist.");
        }

        // Check if this card type already exists for the account
        if (repository.existsByAccountIdAndType(request.getAccountId(), request.getType())) {
            throw new IllegalArgumentException("This account already has a " + request.getType() + " card");
        }

        // Check if account already has 2 cards
        int existingCardCount = repository.countByAccountId(request.getAccountId());
        if (existingCardCount >= 2) {
            throw new IllegalArgumentException("This account already has the maximum allowed number of cards (2)");
        }

        // Step 2: Proceed to save the card
        Card card = Card.builder()
                .cardAlias(request.getCardAlias())
                .accountId(request.getAccountId())
                .type(request.getType())
                .pan(request.getPan())
                .cvv(request.getCvv())
                .build();

        card = repository.save(card);

        // Step 3: Return masked response
        return CardSensitiveResponse.builder()
                .cardId(card.getCardId())
                .cardAlias(card.getCardAlias())
                .accountId(card.getAccountId())
                .type(card.getType())
                .pan(maskPan(card.getPan()))
                .cvv("***")
                .build();
    }

    public Page<CardPublicResponse> search(String cardAlias, CardType type, String pan, Pageable pageable) {
        String aliasLike = (cardAlias != null && !cardAlias.isBlank()) ? "%" + cardAlias.toLowerCase() + "%" : null;
        String panLike = (pan != null && !pan.isBlank()) ? "%" + pan + "%" : null;

        Page<Card> cards = repository.searchCards(aliasLike, type, panLike, pageable);
        return cards.map(this::toPublicResponse);
    }


    public CardPublicResponse getPublicById(UUID id) {
        Card card = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));

        return toPublicResponse(card);
    }


    public CardSensitiveResponse getById(UUID id) {
        Card card = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));
        return toSensitiveResponse(card);
    }

    public CardSensitiveResponse update(UUID id, CardUpdateRequest request) {
        Card card = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));

        //Only update alias
        card.setCardAlias(request.getCardAlias());

        card = repository.save(card);
        return toSensitiveResponse(card);
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Card not found");
        }
        repository.deleteById(id);
    }


    private CardPublicResponse toPublicResponse(Card card) {
        return CardPublicResponse.builder()
                .accountId(card.getAccountId())
                .cardAlias(card.getCardAlias())
                .type(card.getType())
                .pan(maskPan(card.getPan()))
                .build();
    }

    private CardSensitiveResponse toSensitiveResponse(Card card) {
        return CardSensitiveResponse.builder()
                .cardId(card.getCardId())
                .cardAlias(card.getCardAlias())
                .accountId(card.getAccountId())
                .type(card.getType())
                .pan(card.getPan())
                .cvv(card.getCvv())
                .build();
    }

    private String maskPan(String pan) {
        if (pan == null || pan.length() < 4) return "****";
        return "**** **** **** " + pan.substring(pan.length() - 4);
    }

    // New: always return masked response by ID
    public CardSensitiveResponse getMaskedById(UUID id) {
        Card card = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));

        return CardSensitiveResponse.builder()
                .cardId(card.getCardId())
                .cardAlias(card.getCardAlias())
                .accountId(card.getAccountId())
                .type(card.getType())
                .pan(maskPan(card.getPan()))
                .cvv("***")
                .build();
    }

    // update alias, return masked
    public CardSensitiveResponse updateMasked(UUID id, CardUpdateRequest request) {
        Card card = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));

        card.setCardAlias(request.getCardAlias());
        card = repository.save(card);

        return CardSensitiveResponse.builder()
                .cardId(card.getCardId())
                .cardAlias(card.getCardAlias())
                .accountId(card.getAccountId())
                .type(card.getType())
                .pan(maskPan(card.getPan()))
                .cvv("***")
                .build();
    }

}
