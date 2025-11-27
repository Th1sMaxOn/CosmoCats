package org.example.cosmocats;

import org.example.cosmocats.domain.Category;
import org.example.cosmocats.domain.Order;
import org.example.cosmocats.domain.Product;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.service.OrderService;
import org.example.cosmocats.service.ProductService;
import org.example.cosmocats.web.dto.CreateOrderRequest;
import org.example.cosmocats.web.dto.OrderLineDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RepositoryIntegrationTest {

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

        // 3. Замовлення з однією лінією
        CreateOrderRequest request = new CreateOrderRequest(
                "test@example.com",
                "Test User",
                "NEW",
                List.of(
                        new OrderLineDto(
                                savedProduct.getId(),
                                2,
                                BigDecimal.valueOf(100)
                        )
                )
        );

        Order order = orderService.createOrder(request);

        // 4. Перевірки
        assertNotNull(order.getId());
        assertNotNull(order.getOrderNumber());
        assertEquals(1, order.getLines().size());
        assertEquals(2, order.getLines().get(0).getQuantity());
    }
}

