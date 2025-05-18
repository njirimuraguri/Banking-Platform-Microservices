package com.banking.customer_service.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private UUID customerId;
    private String firstName;
    private String lastName;
    private String otherName;

}
