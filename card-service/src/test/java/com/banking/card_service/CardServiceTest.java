package com.banking.card_service;

import com.banking.card_service.client.AccountClient;
import com.banking.card_service.dto.*;
import com.banking.card_service.entity.Card;
import com.banking.card_service.entity.CardType;
import com.banking.card_service.repository.CardRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AccountClient accountClient;

    @InjectMocks
    private CardService cardService;

    private UUID cardId;
    private UUID accountId;
    private Card mockCard;
    private CardRequest mockCardRequest;
    private CardUpdateRequest mockUpdateRequest;
    private AccountDto mockAccountDto;
    private StandardResponse<AccountDto> accountResponse;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        
        // Setup mock card
        mockCard = new Card();
        mockCard.setCardId(cardId);
        mockCard.setCardAlias("Test Card");
        mockCard.setType(CardType.physical);
        mockCard.setPan("1234567890123456");
        mockCard.setCvv("123");
        mockCard.setAccountId(accountId);
        
        // Setup mock card request
        mockCardRequest = new CardRequest();
        mockCardRequest.setAccountId(accountId);
        mockCardRequest.setCardAlias("Test Card");
        mockCardRequest.setType(CardType.physical);
        
        // Setup mock update request
        mockUpdateRequest = new CardUpdateRequest();
        mockUpdateRequest.setCardAlias("Updated Card");
        
        // Setup mock account
        mockAccountDto = new AccountDto();
        mockAccountDto.setAccountId(accountId);
        mockAccountDto.setIban("TEST12345");
        mockAccountDto.setBicSwift("TESTSWIFT");
        
        // Setup mock account response
        accountResponse = StandardResponse.<AccountDto>builder()
                .data(mockAccountDto)
                .message("Account found")
                .build();
    }

    @Test
    void createCard_ValidRequest_ShouldReturnMaskedResponse() {
        // Arrange
        when(accountClient.getAccountById(any(UUID.class))).thenReturn(accountResponse);
        when(cardRepository.existsByAccountIdAndType(any(UUID.class), any(CardType.class))).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(mockCard);

        // Act
        CardSensitiveResponse response = cardService.create(mockCardRequest);

        // Assert
        assertNotNull(response);
        assertEquals(mockCard.getCardAlias(), response.getCardAlias());
        assertEquals(mockCard.getType(), response.getType());
        assertTrue(response.getPan().contains("****"));  // PAN should be masked
        assertNotNull(response.getCvv());  // CVV should be present
        assertEquals(accountId, response.getAccountId());
        
        verify(accountClient).getAccountById(accountId);
        verify(cardRepository).existsByAccountIdAndType(accountId, CardType.physical);
        verify(cardRepository).save(any(Card.class));
    }
    
    @Test
    void createCard_DuplicateCardType_ShouldThrowException() {
        // Arrange
        when(accountClient.getAccountById(any(UUID.class))).thenReturn(accountResponse);
        when(cardRepository.existsByAccountIdAndType(any(UUID.class), any(CardType.class))).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            cardService.create(mockCardRequest)
        );
        
        verify(accountClient).getAccountById(accountId);
        verify(cardRepository).existsByAccountIdAndType(accountId, CardType.physical);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void search_WithParameters_ShouldReturnFilteredCards() {
        // Arrange
        String cardAlias = "Test";
        CardType type = CardType.physical;
        String pan = "1234";
        Pageable pageable = PageRequest.of(0, 10);
        
        List<Card> cardList = Collections.singletonList(mockCard);
        Page<Card> cardPage = new PageImpl<>(cardList, pageable, 1);
        
        when(cardRepository.searchCards(anyString(), any(CardType.class), anyString(), any(Pageable.class)))
            .thenReturn(cardPage);

        // Act
        Page<CardPublicResponse> result = cardService.search(cardAlias, type, pan, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(mockCard.getCardAlias(), result.getContent().get(0).getCardAlias());
        assertEquals(mockCard.getType(), result.getContent().get(0).getType());
        assertTrue(result.getContent().get(0).getPan().contains("****"));  // PAN should be masked
        
        verify(cardRepository).searchCards(contains(cardAlias.toLowerCase()), eq(type), contains(pan), eq(pageable));
    }

    @Test
    void getPublicById_ExistingId_ShouldReturnPublicResponse() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));

        // Act
        CardPublicResponse response = cardService.getPublicById(cardId);

        // Assert
        assertNotNull(response);
        assertEquals(mockCard.getCardAlias(), response.getCardAlias());
        assertEquals(mockCard.getType(), response.getType());
        assertTrue(response.getPan().contains("****"));  // PAN should be masked
        assertEquals(accountId, response.getAccountId());
        
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getPublicById_NonExistingId_ShouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
            cardService.getPublicById(cardId)
        );
        
        verify(cardRepository).findById(cardId);
    }

    @Test
    void updateMasked_ExistingId_ShouldReturnUpdatedResponse() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));
        
        Card updatedCard = new Card();
        updatedCard.setCardId(cardId);
        updatedCard.setCardAlias(mockUpdateRequest.getCardAlias());
        updatedCard.setType(mockCard.getType());
        updatedCard.setPan(mockCard.getPan());
        updatedCard.setCvv(mockCard.getCvv());
        updatedCard.setAccountId(mockCard.getAccountId());
        
        when(cardRepository.save(any(Card.class))).thenReturn(updatedCard);

        // Act
        CardSensitiveResponse response = cardService.updateMasked(cardId, mockUpdateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(mockUpdateRequest.getCardAlias(), response.getCardAlias());
        assertEquals(mockCard.getType(), response.getType());
        assertTrue(response.getPan().contains("****"));  // PAN should be masked
        assertNotNull(response.getCvv());  // CVV should be present
        assertEquals(accountId, response.getAccountId());
        
        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void delete_ExistingId_ShouldDeleteCard() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));
        doNothing().when(cardRepository).deleteById(cardId);

        // Act
        cardService.delete(cardId);

        // Assert
        verify(cardRepository).findById(cardId);
        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void delete_NonExistingId_ShouldThrowException() {
        // Arrange
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
            cardService.delete(cardId)
        );
        
        verify(cardRepository).findById(cardId);
        verify(cardRepository, never()).deleteById(any(UUID.class));
    }
}