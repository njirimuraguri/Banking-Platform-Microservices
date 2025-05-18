package com.banking.card_service.dto;

import com.banking.card_service.entity.CardType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSensitiveResponse {
    private UUID cardId;
    private String cardAlias;
    private UUID accountId;
    private CardType type;
    private String pan;
    private String cvv;
}
