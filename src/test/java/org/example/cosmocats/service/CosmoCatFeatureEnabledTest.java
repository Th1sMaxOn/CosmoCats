package org.example.cosmocats.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        // фіча увімкнена
        "feature.cosmoCats.enabled=true",
        // відключаємо Liquibase, щоб тести не лізли в Postgres
        "spring.liquibase.enabled=false"
})
class CosmoCatFeatureEnabledTest {

    @Autowired
    private CosmoCatService cosmoCatService;

    @Test
    void getCosmoCats_whenFeatureEnabled_returnsList() {
        // викликаємо сервіс
        List<String> cats = cosmoCatService.getCosmoCats();

        assertNotNull(cats);
        assertFalse(cats.isEmpty());
    }
}
