package com.banking.card_service.dto;

import com.banking.card_service.entity.CardType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardPublicResponse {
    private String cardAlias;
    private CardType type;
    private String pan; // masked only
}
