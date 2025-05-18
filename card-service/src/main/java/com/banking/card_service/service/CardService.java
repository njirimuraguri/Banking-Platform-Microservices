package com.banking.card_service.service;

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

    public CardSensitiveResponse create(CardRequest request) {
        Card card = Card.builder()
                .cardAlias(request.getCardAlias())
                .accountId(request.getAccountId())
                .type(request.getType())
                .pan(request.getPan())
                .cvv(request.getCvv())
                .build();

        card = repository.save(card);

        // return masked response after creation
        return CardSensitiveResponse.builder()
                .cardId(card.getCardId())
                .cardAlias(card.getCardAlias())
                .accountId(card.getAccountId())
                .type(card.getType())
                .pan(maskPan(card.getPan()))
                .cvv("***")
                .build();
    }

    public Page<?> search(String cardAlias, CardType type, String pan, boolean showSensitive, Pageable pageable) {
        Page<Card> cards = repository.searchCards(cardAlias, type, pan, pageable);

        if (showSensitive) {
            return cards.map(this::toSensitiveResponse);
        } else {
            return cards.map(this::toPublicResponse);
        }
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

    // New: update alias, return masked
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
