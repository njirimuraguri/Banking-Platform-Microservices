package com.banking.account_service.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private UUID accountId;
    private String iban;
    private String bicSwift;
    private UUID customerId;
}
