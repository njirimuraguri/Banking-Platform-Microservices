package com.banking.card_service.controller;

import com.banking.card_service.dto.*;
import com.banking.card_service.entity.CardType;
import com.banking.card_service.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService service;

    /**
     * Create a new card and return a masked response.
     */
    @PostMapping
    public ResponseEntity<StandardResponse<CardSensitiveResponse>> create(@RequestBody CardRequest request) {
        CardSensitiveResponse response = service.create(request);
        return ResponseEntity.ok(StandardResponse.<CardSensitiveResponse>builder()
                .message("Card created successfully")
                .timestamp(LocalDateTime.now())
                .data(response)
                .build());
    }

    /**
     * Retrieve a paginated list of cards.
     * Supports filtering by alias, type, and PAN.
     * By default, returns masked data unless showSensitive=true.
     */
    @GetMapping("/search")
    public ResponseEntity<StandardResponse<Page<CardPublicResponse>>> searchCardsPublic(
            @RequestParam(required = false) String cardAlias,
            @RequestParam(required = false) CardType type,
            @RequestParam(required = false) String pan,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<CardPublicResponse> result = service.search(cardAlias, type, pan, pageable);
        return ResponseEntity.ok(StandardResponse.<Page<CardPublicResponse>>builder()
                .message("Cards retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(result)
                .build());
    }


    /**
     * Get a card by ID (always returns masked PAN and CVV).
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<StandardResponse<CardPublicResponse>> getById(@PathVariable UUID id) {
        CardPublicResponse card = service.getPublicById(id);  // NEW METHOD
        return ResponseEntity.ok(StandardResponse.<CardPublicResponse>builder()
                .message("Card retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(card)
                .build());
    }


    /**
     * Update only the cardAlias of a card.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<CardSensitiveResponse>> update(
            @PathVariable UUID id,
            @RequestBody CardUpdateRequest request
    ) {
        CardSensitiveResponse updated = service.updateMasked(id, request);
        return ResponseEntity.ok(StandardResponse.<CardSensitiveResponse>builder()
                .message("Card updated successfully")
                .timestamp(LocalDateTime.now())
                .data(updated)
                .build());
    }

    /**
     * Delete a card by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(StandardResponse.<Void>builder()
                .message("Card deleted successfully")
                .timestamp(LocalDateTime.now())
                .data(null)
                .build());
    }
}
