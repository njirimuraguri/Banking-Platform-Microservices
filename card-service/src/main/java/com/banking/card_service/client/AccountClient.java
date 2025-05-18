package com.banking.card_service.client;

import com.banking.card_service.dto.AccountDto;
import com.banking.card_service.dto.StandardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "account-service", url = "${account-service.url}",configuration = com.banking.card_service.config.FeignConfig.class)
public interface AccountClient {

    @GetMapping("/api/accounts/{id}")
    StandardResponse<AccountDto> getAccountById(@PathVariable("id") UUID id);
}
