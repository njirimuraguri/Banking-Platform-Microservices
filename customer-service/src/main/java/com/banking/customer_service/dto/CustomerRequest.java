package com.banking.customer_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String otherName;
}
