package com.banking.customer_service;

import com.banking.customer_service.dto.*;
import com.banking.customer_service.entity.Customer;
import com.banking.customer_service.repository.CustomerRepository;
import com.banking.customer_service.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomer_success() {
        CustomerRequest req = new CustomerRequest("Ian", "Wafula", "Mwangi");
        Customer saved = Customer.builder()
                .customerId(UUID.randomUUID())
                .firstName("Ian")
                .lastName("Wafula")
                .otherName("Mwangi")
                .build();

        when(repository.save(any())).thenReturn(saved);

        CustomerResponse result = service.createCustomer(req);

        assertNotNull(result);
        assertEquals("Ian", result.getFirstName());
    }

    @Test
    void testGetCustomerById_success() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder()
                .customerId(id)
                .firstName("Grace")
                .lastName("Korir")
                .otherName("Ann")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(customer));

        CustomerResponse response = service.getCustomerById(id);

        assertEquals("Grace", response.getFirstName());
    }

    @Test
    void testUpdateCustomer_success() {
        UUID id = UUID.randomUUID();
        Customer existing = Customer.builder()
                .customerId(id)
                .firstName("Jane")
                .lastName("Doe")
                .otherName("Wanjiku")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(existing);

        CustomerRequest update = new CustomerRequest("Janet", "Doe", "Wanjiku");

        CustomerResponse result = service.updateCustomer(id, update);

        assertEquals("Janet", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void testDeleteCustomer_success() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deleteCustomer(id);

        verify(repository).deleteById(id);
    }

    @Test
    void testSearchCustomerByName() {
        String name = "Njiri";
        Pageable pageable = PageRequest.of(0, 5);
        Customer customer = Customer.builder()
                .customerId(UUID.randomUUID())
                .firstName("Njiri")
                .lastName("Muraguri")
                .otherName("Moses")
                .build();

        Page<Customer> page = new PageImpl<>(List.of(customer));
        when(repository.searchCustomers(eq(name), any(), any(), any(), any(), eq(pageable)))
                .thenReturn(page);

        Page<CustomerResponse> result = service.searchCustomers(name, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Njiri");
    }
}
