package com.banking.account_service.client;

import com.banking.account_service.dto.CardPublicDto;
import com.banking.account_service.dto.StandardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "card-service", url = "${card-service.url}")
public interface CardClient {
    @GetMapping("/api/cards/search") // Make sure this matches your updated endpoint
    StandardResponse<Page<CardPublicDto>> getCardsByAlias(@RequestParam String cardAlias);
}
