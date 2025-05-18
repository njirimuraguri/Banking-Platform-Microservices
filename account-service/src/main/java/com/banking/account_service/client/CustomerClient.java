package com.banking.account_service.client;

import com.banking.account_service.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "customer-service") // Service ID as registered in Eureka
public interface CustomerClient {

    @GetMapping("/api/customers/{id}")
    CustomerDto getCustomerById(@PathVariable UUID id);
}
