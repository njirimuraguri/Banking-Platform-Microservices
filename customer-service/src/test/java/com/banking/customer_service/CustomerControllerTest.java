package com.banking.customer_service;

import com.banking.customer_service.controller.CustomerController;
import com.banking.customer_service.dto.*;
import com.banking.customer_service.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID sampleId;
    private CustomerResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleResponse = CustomerResponse.builder()
                .customerId(sampleId)
                .firstName("Ian")
                .lastName("Mwangi")
                .otherName("Wafula")
                .build();
    }

    @Test
    void createCustomer_success() throws Exception {
        CustomerRequest request = new CustomerRequest("Ian", "Mwangi", "Wafula");

        Mockito.when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Ian"))
                .andExpect(jsonPath("$.message").value("Customer created successfully"));
    }

    @Test
    void getCustomerById_success() throws Exception {
        Mockito.when(customerService.getCustomerById(sampleId)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/customers/" + sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Ian"));
    }

    @Test
    void updateCustomer_success() throws Exception {
        CustomerRequest update = new CustomerRequest("Janet", "Doe", "Wanjiku");

        CustomerResponse updated = CustomerResponse.builder()
                .customerId(sampleId)
                .firstName("Janet")
                .lastName("Doe")
                .otherName("Wanjiku")
                .build();

        Mockito.when(customerService.updateCustomer(eq(sampleId), any(CustomerRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/customers/" + sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Janet"))
                .andExpect(jsonPath("$.message").value("Customer updated successfully"));
    }

    @Test
    void deleteCustomer_success() throws Exception {
        mockMvc.perform(delete("/api/customers/" + sampleId))
                .andExpect(status().isOk());
    }

    @Test
    void searchCustomers_success() throws Exception {
        Page<CustomerResponse> mockPage = new PageImpl<>(List.of(sampleResponse));
        Mockito.when(customerService.searchCustomers(any(), any(), any(), any()))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/customers?name=Ian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].firstName").value("Ian"));
    }
}
