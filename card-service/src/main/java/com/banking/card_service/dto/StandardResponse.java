package com.banking.card_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StandardResponse<T> {
    private String message;
    private LocalDateTime timestamp;
    private T data;
}
