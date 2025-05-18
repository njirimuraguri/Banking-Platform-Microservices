package com.banking.customer_service.repository;

import com.banking.customer_service.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @Query(value = "SELECT c FROM Customer c " +
            "WHERE " +
            "(:name IS NULL OR LOWER(CONCAT(c.firstName, ' ', c.lastName, ' ', COALESCE(c.otherName, ''))) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:startStr IS NULL OR c.createdAt >= :startObj) " +
            "AND (:endStr IS NULL OR c.createdAt <= :endObj)")
    Page<Customer> searchCustomers(
            @Param("name") String name,
            @Param("startStr") String startStr,
            @Param("startObj") LocalDateTime start,
            @Param("endStr") String endStr,
            @Param("endObj") LocalDateTime end,
            Pageable pageable
    );


}
