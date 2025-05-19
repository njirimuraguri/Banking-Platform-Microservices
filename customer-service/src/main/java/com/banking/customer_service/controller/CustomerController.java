package com.banking.customer_service.controller;

import com.banking.customer_service.dto.*;
import com.banking.customer_service.repository.CustomerRepository;
import com.banking.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * REST controller for managing customer resources.
 * Supports creation, retrieval, update, deletion, and filtered search.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;
    private final CustomerRepository repository;

    /**
     * Create a new customer.
     */
    @PostMapping
    public ResponseEntity<StandardResponse<CustomerResponse>> createCustomer(
            @RequestBody CustomerRequest request
    ) {
        CustomerResponse created = service.createCustomer(request);

        return ResponseEntity.ok(StandardResponse.<CustomerResponse>builder()
                .message("Customer created successfully")
                .timestamp(LocalDateTime.now())
                .data(created)
                .build());
    }

    /**
     * Get customer by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<CustomerResponse>> getCustomerById(
            @PathVariable UUID id
    ) {
        CustomerResponse customer = service.getCustomerById(id);

        return ResponseEntity.ok(StandardResponse.<CustomerResponse>builder()
                .message("Customer retrieved successfully")
                .timestamp(LocalDateTime.now())
                .data(customer)
                .build());
    }

    /**
     * Update customer name fields.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<CustomerResponse>> updateCustomer(
            @PathVariable UUID id,
            @RequestBody CustomerRequest request
    ) {
        CustomerResponse updated = service.updateCustomer(id, request);

        return ResponseEntity.ok(StandardResponse.<CustomerResponse>builder()
                .message("Customer updated successfully")
                .timestamp(LocalDateTime.now())
                .data(updated)
                .build());
    }

    /**
     * Delete customer by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteCustomer(@PathVariable UUID id) {
        service.deleteCustomer(id);

        return ResponseEntity.ok(StandardResponse.<Void>builder()
                .message("Customer deleted successfully")
                .timestamp(LocalDateTime.now())
                .data(null)
                .build());
    }


    /**
     * Search customers using full-text name search and created date range.
     */
    @GetMapping
    public ResponseEntity<StandardResponse<Page<CustomerResponse>>> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<CustomerResponse> page = service.searchCustomers(name, start, end, pageable);

        String message = page.isEmpty()
                ? "No customers found for the provided name and date range."
                : "Customers fetched successfully";

        return ResponseEntity.ok(
                StandardResponse.<Page<CustomerResponse>>builder()
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .data(page)
                        .build()
        );
    }

}
