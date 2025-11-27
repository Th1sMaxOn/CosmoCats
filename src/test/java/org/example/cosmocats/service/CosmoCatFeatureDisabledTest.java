package org.example.cosmocats.service;

import org.example.cosmocats.feature.FeatureNotAvailableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(properties = {
        // фіча вимкнена
        "feature.cosmoCats.enabled=false",
        // відключаємо Liquibase, щоб не було конектів до БД у CI
        "spring.liquibase.enabled=false"
})
class CosmoCatFeatureDisabledTest {

    @Autowired
    private CosmoCatService cosmoCatService;

    @Test
    void getCosmoCats_whenFeatureDisabled_throws() {
        assertThrows(FeatureNotAvailableException.class,
                () -> cosmoCatService.getCosmoCats());
    }
}
