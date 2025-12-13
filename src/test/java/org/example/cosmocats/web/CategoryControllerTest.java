package org.example.cosmocats.web;

import org.example.cosmocats.AbstractIntegrationTest;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.web.dto.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest extends AbstractIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    // ==========================================
    // CREATE (POST) - @PreAuthorize("hasAnyRole('ADMIN', 'BOT')")
    // ==========================================

    @Test
    @DisplayName("POST /api/v1/categories - Fail: 401 Unauthorized (Без аутентифікації)")
    void createCategory_noAuth_returnsUnauthorized() throws Exception {
        // Given
        CategoryDto request = new CategoryDto(null, "NoAuth", "Test");

        // When & Then
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/v1/categories - Fail: 403 Forbidden (Роль USER)")
    void createCategory_userRole_returnsForbidden() throws Exception {
        // Given
        CategoryDto request = new CategoryDto(null, "WrongRole", "Test");

        // When & Then
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/categories - Success: 201 Created (Роль ADMIN)")
    void createCategory_validPayload_returnsCreated() throws Exception {
        // Given
        CategoryDto request = new CategoryDto(null, "Space Food", "Food for cosmo cats");

        // When & Then
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Space Food"))
                .andExpect(jsonPath("$.description").value("Food for cosmo cats"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/categories - Fail: пусте ім'я -> 400 Bad Request")
    void createCategory_invalidName_returnsBadRequest() throws Exception {
        // Порушуємо @NotBlank
        CategoryDto request = new CategoryDto(null, "", "Desc");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Content"));
    }

    // ==========================================
    // GET ALL (GET) - Authenticated only
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/categories - Fail: 401 Unauthorized (Без аутентифікації)")
    void getAll_noAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/v1/categories - Success: 200 OK (Роль USER)")
    void getAll_returnsList() throws Exception {
        // Given
        categoryRepository.saveAll(List.of(
                new CategoryEntity(null, "Cat 1", "Desc 1"),
                new CategoryEntity(null, "Cat 2", "Desc 2")
        ));

        // When & Then
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Cat 1", "Cat 2")));
    }

    // ==========================================
    // GET BY ID (GET /{id}) - Authenticated only
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/categories/{id} - Fail: 401 Unauthorized (Без аутентифікації)")
    void getById_noAuth_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/v1/categories/{id} - Success: повертає категорію")
    void getById_existingId_returnsCategory() throws Exception {
        // Given
        CategoryEntity saved = categoryRepository.save(new CategoryEntity(null, "Target Cat", "Target Desc"));

        // When & Then
        mockMvc.perform(get("/api/v1/categories/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Target Cat"))
                .andExpect(jsonPath("$.description").value("Target Desc"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/categories/{id} - Fail: неіснуючий ID -> 404 Not Found")
    void getById_nonExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/categories/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(containsString("Category not found with identifier: 9999")));
    }

    // ==========================================
    // UPDATE (PUT /{id}) - @PreAuthorize("hasRole('ADMIN')")
    // ==========================================

    @Test
    @DisplayName("PUT /api/v1/categories/{id} - Fail: 401 Unauthorized (Без аутентифікації)")
    void update_noAuth_returnsUnauthorized() throws Exception {
        // Given
        CategoryEntity saved = categoryRepository.save(new CategoryEntity(null, "Old Name", "Old Desc"));
        CategoryDto updateRequest = new CategoryDto(null, "New Name", "New Desc");

        // When & Then
        mockMvc.perform(put("/api/v1/categories/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /api/v1/categories/{id} - Fail: 403 Forbidden (Роль USER)")
    void update_userRole_returnsForbidden() throws Exception {
        // Given
        CategoryEntity saved = categoryRepository.save(new CategoryEntity(null, "Old Name", "Old Desc"));
        CategoryDto updateRequest = new CategoryDto(null, "New Name", "New Desc");

        // When & Then
        mockMvc.perform(put("/api/v1/categories/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/v1/categories/{id} - Success: 200 OK (Роль ADMIN)")
    void update_existingId_returnsUpdatedCategory() throws Exception {
        // Given
        CategoryEntity saved = categoryRepository.save(new CategoryEntity(null, "Old Name", "Old Desc"));
        CategoryDto updateRequest = new CategoryDto(null, "New Name", "New Desc");

        // When & Then
        mockMvc.perform(put("/api/v1/categories/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.description").value("New Desc"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/v1/categories/{id} - Fail: неіснуючий ID -> 404 Not Found")
    void update_nonExistingId_returnsNotFound() throws Exception {
        CategoryDto updateRequest = new CategoryDto(null, "Name", "Desc");

        mockMvc.perform(put("/api/v1/categories/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/v1/categories/{id} - Fail: невалідний DTO -> 400 Bad Request")
    void update_invalidData_returnsBadRequest() throws Exception {
        // Given
        CategoryEntity saved = categoryRepository.save(new CategoryEntity(null, "Valid", "Valid"));
        CategoryDto invalidRequest = new CategoryDto(null, "", "Valid"); // Порожнє ім'я

        // When & Then
        mockMvc.perform(put("/api/v1/categories/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // DELETE (DELETE /{id}) - @PreAuthorize("hasRole('ADMIN')")
    // ==========================================

    @Test
    @DisplayName("DELETE /api/v1/categories/{id} - Fail: 401 Unauthorized (Без аутентифікації)")
    void delete_noAuth_returnsUnauthorized() throws Exception {
        // Given
        CategoryEntity saved = categoryRepository.save(new CategoryEntity(null, "To Delete", "Desc"));

        // When
        mockMvc.perform(delete("/api/v1/categories/{id}", saved.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/v1/categories/{id} - Fail: 403 Forbidden (Роль USER)")
    void delete_userRole_returnsForbidden() throws Exception {
        // Given
        CategoryEntity saved = categoryRepository.save(new CategoryEntity(null, "To Delete", "Desc"));

        // When
        mockMvc.perform(delete("/api/v1/categories/{id}", saved.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/v1/categories/{id} - Success: 204 No Content (Роль ADMIN)")
    void delete_existingId_returnsNoContent() throws Exception {
        // Given
        CategoryEntity saved = categoryRepository.save(new CategoryEntity(null, "To Delete", "Desc"));

        // When
        mockMvc.perform(delete("/api/v1/categories/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }
}