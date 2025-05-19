package com.banking.customer_service;

import com.banking.customer_service.entity.Customer;
import com.banking.customer_service.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void searchCustomersByName_shouldReturnMatchingCustomers() {
        Customer c1 = Customer.builder()
                .firstName("Ian")
                .lastName("Mwangi")
                .otherName("Wafula")
                .createdAt(LocalDateTime.now())
                .build();

        Customer c2 = Customer.builder()
                .firstName("Grace")
                .lastName("Mwangi")
                .otherName("Ann")
                .createdAt(LocalDateTime.now())
                .build();

        customerRepository.save(c1);
        customerRepository.save(c2);

        String name = "Ian";
        LocalDateTime now = LocalDateTime.now();

        Page<Customer> result = customerRepository.searchCustomers(
                name,                  // name
                null,                  // startStr
                null,                  // startObj
                null,                  // endStr
                null,                  // endObj
                PageRequest.of(0, 10)  // pageable
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Ian");
    }
}

