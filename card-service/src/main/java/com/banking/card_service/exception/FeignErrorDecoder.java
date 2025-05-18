package com.banking.card_service.exception;

import com.banking.card_service.dto.StandardResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

@Component
@Slf4j
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;
    private final ErrorDecoder.Default defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try (InputStream bodyIs = response.body().asInputStream()) {
            StandardResponse<?> error = objectMapper.readValue(bodyIs, StandardResponse.class);
            return new NoSuchElementException(error.getMessage());
        } catch (IOException e) {
            log.error("Failed to parse error from Feign client", e);
            return new IllegalArgumentException("Failed to contact account-service: " + response.reason());
        }
    }
}
