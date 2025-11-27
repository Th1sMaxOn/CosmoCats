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

@SpringBootTest
@Testcontainers
class CosmoCatFeatureDisabledTest {

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
        // важливо для CI: явно вказуємо dialect
        registry.add("spring.jpa.database-platform",
                () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private FeatureToggleService featureToggleService;

    @Test
    void getProducts_whenFeatureDisabled_throws() {
        // вимикаємо фічу
        featureToggleService.setFeatureEnabled(false);

        // очікуємо, що сервіс кине RuntimeException
        Assertions.assertThrows(RuntimeException.class, () -> {
            productService.getAll();
        });
    }
}
