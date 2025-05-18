package com.banking.card_service.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AccountDto {
    private UUID accountId;
    private String iban;
    private String bicSwift;
    private UUID customerId;
}
