package com.banking.card_service;

import com.banking.card_service.controller.CardController;
import com.banking.card_service.dto.*;
import com.banking.card_service.entity.CardType;
import com.banking.card_service.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private UUID cardId;
    private UUID accountId;
    private CardRequest mockCardRequest;
    private CardUpdateRequest mockUpdateRequest;
    private CardSensitiveResponse mockSensitiveResponse;
    private CardPublicResponse mockPublicResponse;
    private Page<CardPublicResponse> mockCardPage;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        
        // Setup mock request
        mockCardRequest = new CardRequest();
        mockCardRequest.setAccountId(accountId);
        mockCardRequest.setCardAlias("Test Card");
        mockCardRequest.setType(CardType.physical);
        
        // Setup mock update request
        mockUpdateRequest = new CardUpdateRequest();
        mockUpdateRequest.setCardAlias("Updated Card");
        
        // Setup mock sensitive response
        mockSensitiveResponse = new CardSensitiveResponse();
        mockSensitiveResponse.setCardAlias("Test Card");
        mockSensitiveResponse.setType(CardType.physical);
        mockSensitiveResponse.setPan("**** **** **** 3456");
        mockSensitiveResponse.setCvv("***");
        mockSensitiveResponse.setAccountId(accountId);
        
        // Setup mock public response
        mockPublicResponse = new CardPublicResponse();
        mockPublicResponse.setCardAlias("Test Card");
        mockPublicResponse.setType(CardType.physical);
        mockPublicResponse.setPan("**** **** **** 3456");
        mockPublicResponse.setAccountId(accountId);
        
        // Setup mock page
        mockCardPage = new PageImpl<>(
            Collections.singletonList(mockPublicResponse),
            PageRequest.of(0, 10),
            1
        );
    }

    @Test
    void create_ValidRequest_ShouldReturnCreatedCard() {
        // Arrange
        when(cardService.create(any(CardRequest.class))).thenReturn(mockSensitiveResponse);

        // Act
        ResponseEntity<StandardResponse<CardSensitiveResponse>> response = 
            cardController.create(mockCardRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Card created successfully", response.getBody().getMessage());
        assertEquals(mockSensitiveResponse, response.getBody().getData());
        
        verify(cardService).create(mockCardRequest);
    }

    @Test
    void searchCardsPublic_WithParameters_ShouldReturnFilteredCards() {
        // Arrange
        String cardAlias = "Test";
        CardType type = CardType.physical;
        String pan = "1234";
        Pageable pageable = PageRequest.of(0, 10);
        
        when(cardService.search(anyString(), any(CardType.class), anyString(), any(Pageable.class)))
            .thenReturn(mockCardPage);

        // Act
        ResponseEntity<StandardResponse<Page<CardPublicResponse>>> response = 
            cardController.searchCardsPublic(cardAlias, type, pan, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cards retrieved successfully", response.getBody().getMessage());
        assertEquals(mockCardPage, response.getBody().getData());
        
        verify(cardService).search(cardAlias, type, pan, pageable);
    }

    @Test
    void getById_ExistingId_ShouldReturnCard() {
        // Arrange
        when(cardService.getPublicById(cardId)).thenReturn(mockPublicResponse);

        // Act
        ResponseEntity<StandardResponse<CardPublicResponse>> response = 
            cardController.getById(cardId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Card retrieved successfully", response.getBody().getMessage());
        assertEquals(mockPublicResponse, response.getBody().getData());
        
        verify(cardService).getPublicById(cardId);
    }

    @Test
    void getById_NonExistingId_ShouldThrowException() {
        // Arrange
        when(cardService.getPublicById(cardId)).thenThrow(new NoSuchElementException("Card not found"));

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
            cardController.getById(cardId)
        );
        
        verify(cardService).getPublicById(cardId);
    }

    @Test
    void update_ExistingId_ShouldReturnUpdatedCard() {
        // Arrange
        when(cardService.updateMasked(any(UUID.class), any(CardUpdateRequest.class)))
            .thenReturn(mockSensitiveResponse);

        // Act
        ResponseEntity<StandardResponse<CardSensitiveResponse>> response = 
            cardController.update(cardId, mockUpdateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Card updated successfully", response.getBody().getMessage());
        assertEquals(mockSensitiveResponse, response.getBody().getData());
        
        verify(cardService).updateMasked(cardId, mockUpdateRequest);
    }

    @Test
    void delete_ExistingId_ShouldReturnSuccessResponse() {
        // Arrange
        doNothing().when(cardService).delete(cardId);

        // Act
        ResponseEntity<StandardResponse<Void>> response = 
            cardController.delete(cardId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Card deleted successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        
        verify(cardService).delete(cardId);
    }
}