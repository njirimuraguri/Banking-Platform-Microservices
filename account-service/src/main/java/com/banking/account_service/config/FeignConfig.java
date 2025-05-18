package com.banking.account_service.config;

import com.banking.account_service.exception.FeignErrorDecoder;
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