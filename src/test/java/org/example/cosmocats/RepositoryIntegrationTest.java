package org.example.cosmocats;

import org.example.cosmocats.domain.Category;
import org.example.cosmocats.domain.Order;
import org.example.cosmocats.domain.Product;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.service.OrderService;
import org.example.cosmocats.service.ProductService;
import org.example.cosmocats.web.dto.CreateOrderRequest;
import org.example.cosmocats.web.dto.OrderLineDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@Testcontainers
class RepositoryIntegrationTest {

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
    }

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Test
    void fullCrudFlow_createsOrderWithLines() {
        // 1. Категорія
        Category cat = new Category();
        cat.setName("Test Category");
        cat.setDescription("For integration test");
        Category savedCategory = categoryRepository.save(cat);

        // 2. Продукт у цій категорії
        Product product = new Product();
        product.setProductName("Test Product");
        product.setDescription("Test description");
        product.setPrice(BigDecimal.valueOf(100));
        Product savedProduct = productService.create(product, savedCategory.getId());

        // 3. Замовлення з одним рядком
        CreateOrderRequest request = new CreateOrderRequest(
                "test@example.com",
                "Test User",
                "NEW",
                List.of(new OrderLineDto(
                        savedProduct.getId(),
                        2,
                        BigDecimal.valueOf(100)
                ))
        );

        Order order = orderService.createOrder(request);

        // 4. Перевірки
        Assertions.assertNotNull(order.getId());
        Assertions.assertNotNull(order.getOrderNumber());
        Assertions.assertEquals(1, order.getLines().size());
        Assertions.assertEquals(2, order.getLines().get(0).getQuantity());
    }
}
