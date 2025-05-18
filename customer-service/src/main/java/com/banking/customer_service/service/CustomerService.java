// File: CustomerService.java
package com.banking.customer_service.service;

import com.banking.customer_service.dto.*;
import com.banking.customer_service.entity.Customer;
import com.banking.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Service class responsible for customer business logic and persistence.
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    /**
     * Create a new customer from request data.
     */
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .otherName(request.getOtherName())
                .createdAt(LocalDateTime.now())
                .build();

        Customer saved = repository.save(customer);
        return toResponse(saved);
    }

    /**
     * Retrieve customer by ID.
     */
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer with ID " + id + " not found"));

        return toResponse(customer);
    }


    /**
     * Search for customers by name (partial match) and creation date range.
     */
    public Page<CustomerResponse> searchCustomers(String name, LocalDateTime start, LocalDateTime end, Pageable pageable){
        String startStr = start != null ? start.toString() : null;
        String endStr = end != null ? end.toString() : null;
        return repository.searchCustomers(name, startStr, start, endStr, end, pageable)
                .map(this::toResponse);
    }

    /**
     * Update an existing customer's name fields.
     */
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setOtherName(request.getOtherName());

        customer = repository.save(customer);
        return toResponse(customer);
    }

    /**
     * Delete a customer by ID.
     */
    public void deleteCustomer(UUID id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Customer not found");
        }
        repository.deleteById(id);
    }

    /**
     * Convert a Customer entity to a response DTO.
     */
    private CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .otherName(customer.getOtherName())
                .build();
    }
}
