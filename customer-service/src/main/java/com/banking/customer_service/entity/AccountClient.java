package com.banking.customer_service.entity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "account-service")
public interface AccountClient {
    @GetMapping("/api/accounts/exists-by-customer/{customerId}")
    boolean hasAccountsForCustomer(@PathVariable UUID customerId);
}
