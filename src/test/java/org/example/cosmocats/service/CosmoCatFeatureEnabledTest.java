package org.example.cosmocats.service;

import org.example.cosmocats.feature.FeatureToggleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest
@Testcontainers
class CosmoCatFeatureEnabledTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("cosmocats")
            .withUsername("cosmocats")
            .withPassword("cosmocats");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // важливо для CI
        registry.add("spring.jpa.database-platform",
                () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private FeatureToggleService featureToggleService;

    @Test
    void getCosmoCats_whenFeatureEnabled_returnsList() {
        // вмикаємо фічу
        featureToggleService.setFeatureEnabled(true);

        // викликаємо метод сервісу – тут має бути саме той метод, який у тебе є
        // у ProductService (ти раніше показував getAll())
        List<?> products = productService.getAll();

        Assertions.assertFalse(products.isEmpty());
    }
}
