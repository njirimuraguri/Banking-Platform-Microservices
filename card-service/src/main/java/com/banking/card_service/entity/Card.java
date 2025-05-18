package com.banking.card_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID cardId;

    private String cardAlias;

    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CardType type; // virtual or physical

    private String pan; // sensitive

    private String cvv; // sensitive

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
