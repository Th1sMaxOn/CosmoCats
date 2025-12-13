package org.example.cosmocats.web;

import org.example.cosmocats.AbstractIntegrationTest;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.repository.CustomerRepository;
import org.example.cosmocats.repository.OrderRepository;
import org.example.cosmocats.repository.ProductRepository;
import org.example.cosmocats.repository.entity.*;
import org.example.cosmocats.web.dto.CreateOrderRequest;
import org.example.cosmocats.web.dto.OrderLineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest extends AbstractIntegrationTest {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CustomerRepository customerRepository;

    private ProductEntity savedProduct;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // 1. Створюємо Категорію
        CategoryEntity category = categoryRepository.save(
                new CategoryEntity(null, "Test Cat", "Desc")
        );

        // 2. Створюємо Продукт (він треба для OrderLine)
        savedProduct = productRepository.save(
                new ProductEntity(null, "Test Product", "Desc", BigDecimal.valueOf(100.00), category)
        );
    }

    // ==========================================
    // CREATE (POST)
    // ==========================================

    @Test
    @DisplayName("POST /api/v1/orders - Success: створює замовлення, кастомера і рахує суму")
    void create_validRequest_returnsCreated() throws Exception {
        // Given
        OrderLineDto lineDto = new OrderLineDto(savedProduct.getId(), 2, null); // 2 шт по 100.00
        CreateOrderRequest request = new CreateOrderRequest(
                "newuser@example.com",
                "New User",
                "NEW",
                List.of(lineDto)
        );

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.customerEmail").value("newuser@example.com"))
                .andExpect(jsonPath("$.customerFullName").value("New User"))
                .andExpect(jsonPath("$.totalAmount").value(200.00)) // 2 * 100
                .andExpect(jsonPath("$.lines", hasSize(1)))
                .andExpect(jsonPath("$.lines[0].productId").value(savedProduct.getId()));
    }

    @Test
    @DisplayName("POST /api/v1/orders - Fail: неіснуючий продукт -> 404 Not Found")
    void create_nonExistingProduct_returnsNotFound() throws Exception {
        // Given
        long fakeId = 9999L;
        CreateOrderRequest request = new CreateOrderRequest(
                "user@test.com", "User", "NEW",
                List.of(new OrderLineDto(fakeId, 1, null))
        );

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(containsString(String.valueOf(fakeId))));
    }

    @Test
    @DisplayName("POST /api/v1/orders - Fail: невалідний запит (пустий email) -> 400 Bad Request")
    void create_invalidEmail_returnsBadRequest() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                "", "User", "NEW", // Пустий email
                List.of(new OrderLineDto(savedProduct.getId(), 1, null))
        );

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed"));
    }

    // ==========================================
    // GET ALL & FILTER (GET)
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/orders - Success: повертає всі замовлення")
    void getAll_returnsList() throws Exception {
        createTestOrder("u1@test.com", "ORD-001");
        createTestOrder("u2@test.com", "ORD-002");

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/v1/orders?email=... - Success: фільтрує по email")
    void getAll_filterByEmail_returnsFiltered() throws Exception {
        createTestOrder("target@test.com", "ORD-TARGET");
        createTestOrder("other@test.com", "ORD-OTHER");

        mockMvc.perform(get("/api/v1/orders")
                        .param("email", "target@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderNumber").value("ORD-TARGET"));
    }

    // ==========================================
    // GET BY ID / NUMBER (GET)
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/orders/{orderNumber} - Success: пошук по Natural ID")
    void getByOrderNumber_existing_returnsOrder() throws Exception {
        OrderEntity saved = createTestOrder("user@test.com", "ORD-12345");

        mockMvc.perform(get("/api/v1/orders/{orderNumber}", "ORD-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.orderNumber").value("ORD-12345"));
    }

    @Test
    @DisplayName("GET /api/v1/orders/id/{id} - Success: пошук по Primary Key")
    void getById_existing_returnsOrder() throws Exception {
        OrderEntity saved = createTestOrder("user@test.com", "ORD-PK-TEST");

        mockMvc.perform(get("/api/v1/orders/id/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-PK-TEST"));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{orderNumber} - Fail: неіснуючий номер -> 404")
    void getByOrderNumber_missing_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderNumber}", "MISSING-ORD"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // UPDATE STATUS (PATCH)
    // ==========================================

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderNumber}/status - Success: змінює статус")
    void updateStatus_existing_returnsUpdated() throws Exception {
        createTestOrder("user@test.com", "ORD-STATUS");

        mockMvc.perform(patch("/api/v1/orders/{orderNumber}/status", "ORD-STATUS")
                        .param("newStatus", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-STATUS"))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderNumber}/status - Fail: неіснуючий номер -> 404")
    void updateStatus_missing_returnsNotFound() throws Exception {
        mockMvc.perform(patch("/api/v1/orders/{orderNumber}/status", "MISSING")
                        .param("newStatus", "SHIPPED"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // DELETE (DELETE)
    // ==========================================

    @Test
    @DisplayName("DELETE /api/v1/orders/{id} - Success: видаляє і каскадно чистить лінії -> 204")
    void delete_existing_returnsNoContent() throws Exception {
        OrderEntity saved = createTestOrder("del@test.com", "ORD-DEL");

        mockMvc.perform(delete("/api/v1/orders/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    // --- HELPER ---
    // Створює замовлення прямо в базі, щоб не дублювати код в тестах
    private OrderEntity createTestOrder(String email, String orderNumber) {
        CustomerEntity customer = customerRepository.findByEmail(email)
                .orElseGet(() -> customerRepository.save(
                        CustomerEntity.builder().email(email).fullName("Test User").build()
                ));

        OrderEntity order = OrderEntity.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .status("NEW")
                .createdAt(Instant.now())
                .build();

        // Зберігаємо замовлення
        order = orderRepository.save(order);

        // Додаємо лінію (важливо прив'язати до order)
        OrderLineEntity line = OrderLineEntity.builder()
                .order(order)
                .product(savedProduct)
                .quantity(1)
                .priceAtPurchase(savedProduct.getPrice())
                .build();

        order.setLines(Collections.singletonList(line));
        return orderRepository.save(order);
    }
}
