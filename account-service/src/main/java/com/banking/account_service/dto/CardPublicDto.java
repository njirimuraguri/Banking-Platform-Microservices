package com.banking.account_service.dto;

import com.banking.account_service.entity.CardType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class CardPublicDto {
    private String cardAlias;
    private CardType type;
    private String pan;
    @JsonProperty("accountId")
    private UUID accountId;
}
