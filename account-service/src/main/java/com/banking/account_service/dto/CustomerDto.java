package com.banking.account_service.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private UUID customerId;
    private String firstName;
    private String lastName;
    private String otherName;
}
