package com.banking.card_service.config;

import com.banking.card_service.exception.FeignErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder(FeignErrorDecoder decoder) {
        return decoder;
    }
}
