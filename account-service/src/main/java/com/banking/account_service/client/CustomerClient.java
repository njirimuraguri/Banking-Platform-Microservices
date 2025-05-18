package com.banking.account_service.client;

import com.banking.account_service.dto.CustomerDto;
import com.banking.account_service.dto.StandardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "customer-service",
        url = "${customer-service.url}"
)
public interface CustomerClient {
    @GetMapping("/api/customers/{id}")
    StandardResponse<CustomerDto> getCustomerById(@PathVariable("id") UUID id);
}