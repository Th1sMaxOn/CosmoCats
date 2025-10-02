package com.cosmo.cats.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PriceRateClient {

    private final RestClient client;

    public PriceRateClient(@Value("${rates.base-url:http://localhost:9090}") String baseUrl) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    RestClient restClient() { return client; }

    public PriceRateDTO getRate(String from, String to) {
        return client.get()
                .uri("/rates?from={from}&to={to}", from, to)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(PriceRateDTO.class);
    }
}
