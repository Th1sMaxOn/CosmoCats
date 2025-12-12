package org.example.cosmocats.web;

import org.example.cosmocats.AbstractIntegrationTest;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.repository.ProductRepository;
import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.repository.entity.ProductEntity;
import org.example.cosmocats.web.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private CategoryEntity savedCategory;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // Створюємо базову категорію для тестів
        savedCategory = categoryRepository.save(
                new CategoryEntity(null, "Test Category", "For products")
        );
    }

    // ==========================================
    // CREATE (POST)
    // ==========================================

    @Test
    @DisplayName("POST /api/v1/products - Success: створює продукт")
    void createProduct_validPayload_returnsCreated() throws Exception {
        // Given
        ProductDto request = new ProductDto(
                null,
                "Cosmo Snack",
                "Tasty",
                BigDecimal.valueOf(10.50),
                savedCategory.getId() // Посилаємось на існуючу категорію
        );

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.productName").value("Cosmo Snack"))
                .andExpect(jsonPath("$.price").value(10.50))
                .andExpect(jsonPath("$.categoryId").value(savedCategory.getId()));
    }

    @Test
    @DisplayName("POST /api/v1/products - Fail: неіснуюча категорія -> 404 Not Found")
    void createProduct_nonExistingCategory_returnsNotFound() throws Exception {
        // Given: ID категорії, якого немає в базі
        long nonExistentCatId = 9999L;
        ProductDto request = new ProductDto(null, "Bad Cat", "Desc", BigDecimal.TEN, nonExistentCatId);

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(containsString(String.valueOf(nonExistentCatId))));
    }

    @Test
    @DisplayName("POST /api/v1/products - Fail: невалідна ціна -> 400 Bad Request")
    void createProduct_invalidPrice_returnsBadRequest() throws Exception {
        // Given: ціна 0 або від'ємна (порушує @Positive)
        ProductDto request = new ProductDto(null, "Cheap", "Desc", BigDecimal.ZERO, savedCategory.getId());

        // When & Then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed"));
    }

    // ==========================================
    // GET ALL (GET)
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/products - Success: повертає список")
    void getAll_returnsList() throws Exception {
        // Given
        productRepository.saveAll(List.of(
                new ProductEntity(null, "P1", "D1", BigDecimal.TEN, savedCategory),
                new ProductEntity(null, "P2", "D2", BigDecimal.ONE, savedCategory)
        ));

        // When & Then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].productName", containsInAnyOrder("P1", "P2")));
    }

    // ==========================================
    // GET BY ID (GET /{id})
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/products/{id} - Success: повертає продукт")
    void getById_existingId_returnsProduct() throws Exception {
        // Given
        ProductEntity saved = productRepository.save(
                new ProductEntity(null, "Target Product", "Desc", BigDecimal.valueOf(100), savedCategory)
        );

        // When & Then
        mockMvc.perform(get("/api/v1/products/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.productName").value("Target Product"))
                .andExpect(jsonPath("$.categoryId").value(savedCategory.getId()));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Fail: неіснуючий ID -> 404")
    void getById_nonExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // GET BY CATEGORY (GET /by-category/{id})
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/products/by-category/{id} - Success: фільтрує по категорії")
    void byCategory_existingCategory_returnsList() throws Exception {
        // Given
        // Продукт в правильній категорії
        productRepository.save(new ProductEntity(null, "In Cat", "Desc", BigDecimal.TEN, savedCategory));

        // Інша категорія і продукт в ній
        CategoryEntity otherCat = categoryRepository.save(new CategoryEntity(null, "Other", "Desc"));
        productRepository.save(new ProductEntity(null, "Other Prod", "Desc", BigDecimal.TEN, otherCat));

        // When & Then: просимо тільки для savedCategory
        mockMvc.perform(get("/api/v1/products/by-category/{id}", savedCategory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Тільки один
                .andExpect(jsonPath("$[0].productName").value("In Cat"));
    }

    @Test
    @DisplayName("GET /api/v1/products/by-category/{id} - Fail: категорія не існує -> 404")
    void byCategory_nonExistingCategory_returnsNotFound() throws Exception {
        // Сервіс перевіряє categoryRepository.existsById перед пошуком
        mockMvc.perform(get("/api/v1/products/by-category/{id}", 8888L))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // GET POPULAR (GET /popular)
    // ==========================================
    @Test
    @DisplayName("GET /api/v1/products/popular - Success: повертає список (навіть якщо пустий)")
    void popular_returnsList() throws Exception {
        // Тут ми не створюємо Order/OrderLines, бо це складно для Unit/Integration тесту контролера.
        // Перевіряємо просто контракт, що метод не падає і вертає JSON array.
        mockMvc.perform(get("/api/v1/products/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ==========================================
    // UPDATE (PUT /{id})
    // ==========================================

    @Test
    @DisplayName("PUT /api/v1/products/{id} - Success: оновлює продукт")
    void update_existingId_returnsUpdated() throws Exception {
        // Given
        ProductEntity saved = productRepository.save(
                new ProductEntity(null, "Old Name", "Old", BigDecimal.valueOf(50), savedCategory)
        );
        ProductDto updateRequest = new ProductDto(null, "New Name", "New", BigDecimal.valueOf(99), savedCategory.getId());

        // When & Then
        mockMvc.perform(put("/api/v1/products/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("New Name"))
                .andExpect(jsonPath("$.price").value(99));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} - Fail: неіснуючий ID -> 404")
    void update_nonExistingId_returnsNotFound() throws Exception {
        ProductDto updateRequest = new ProductDto(null, "Name", "Desc", BigDecimal.TEN, savedCategory.getId());

        mockMvc.perform(put("/api/v1/products/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // DELETE (DELETE /{id})
    // ==========================================

    @Test
    @DisplayName("DELETE /api/v1/products/{id} - Success: видаляє існуючий -> 204")
    void delete_existingId_returnsNoContent() throws Exception {
        // Given
        ProductEntity saved = productRepository.save(
                new ProductEntity(null, "To Delete", "Desc", BigDecimal.TEN, savedCategory)
        );

        // When
        mockMvc.perform(delete("/api/v1/products/{id}", saved.getId()))
                .andExpect(status().isNoContent()); // 204

        // Then: в базі немає
        mockMvc.perform(get("/api/v1/products/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} - Idempotent: видаляє неіснуючий -> 204 (НЕ 404!)")
    void delete_nonExistingId_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/products/{id}", 99999L))
                .andExpect(status().isNoContent());
    }
}