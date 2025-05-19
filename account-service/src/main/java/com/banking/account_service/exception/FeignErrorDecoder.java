package com.banking.account_service.exception;

import com.banking.account_service.dto.StandardResponse;
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
            // Try to parse the error response from the customer service
            StandardResponse<?> errorResponse = objectMapper.readValue(bodyIs, StandardResponse.class);

            // Convert to a CustomerNotFoundException that your GlobalExceptionHandler can process
            return new NoSuchElementException(errorResponse.getMessage());

        } catch (IOException e) {
            log.error("Error parsing feign client error response", e);
            // If can't parse the response, just return a generic error
            return new IllegalArgumentException("Error communicating with customer service: " + response.reason());
        }
    }
}