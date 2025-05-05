package com.proton.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.io.IOException;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .errorHandler(new DefaultResponseErrorHandler() {
                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        // Não lançar exceção para códigos 4xx/5xx
                    }
                })
                .build();
    }
}