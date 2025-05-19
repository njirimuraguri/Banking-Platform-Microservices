package com.banking.account_service;

import com.banking.account_service.controller.AccountController;
import com.banking.account_service.dto.*;
import com.banking.account_service.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAccount() throws Exception {
        AccountRequest req = new AccountRequest("KE123", "JUBANK", UUID.randomUUID());
        AccountResponse res = AccountResponse.builder()
                .accountId(UUID.randomUUID())
                .iban("KE123")
                .bicSwift("JUBANK")
                .customerId(req.getCustomerId())
                .build();

        when(service.createAccount(any())).thenReturn(res);

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account created successfully"))
                .andExpect(jsonPath("$.data.iban").value("KE123"));
    }

    @Test
    void testGetAccountById() throws Exception {
        UUID id = UUID.randomUUID();
        AccountResponse res = AccountResponse.builder().accountId(id).iban("KE123").build();
        when(service.getAccount(id)).thenReturn(res);

        mockMvc.perform(get("/api/accounts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("KE123"));
    }
}
