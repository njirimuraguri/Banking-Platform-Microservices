package com.banking.account_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountUpdateRequest {
    private String iban;
    private String bicSwift;
}
