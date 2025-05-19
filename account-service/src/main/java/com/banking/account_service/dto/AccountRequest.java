package com.banking.account_service.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {
    private String iban;
    private String bicSwift;
    private UUID customerId;
}
