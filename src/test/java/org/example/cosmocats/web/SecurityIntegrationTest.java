package org.example.cosmocats.web;

import org.example.cosmocats.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIntegrationTest extends AbstractIntegrationTest {

    // API ключ для ROLE_BOT (з application.yml)
    private static final String VALID_BOT_API_KEY = "cosmos-cat-bot-secret-x99";

    // Ендпоінт для створення категорій: @PreAuthorize("hasAnyRole('ADMIN', 'BOT')")
    private static final String PROTECTED_ENDPOINT = "/api/v1/categories";

    // Приклад DTO (для POST)
    private static final String DUMMY_CATEGORY_JSON = "{\"name\": \"Test\", \"description\": \"Test\"}";

    // ==========================================
    // 1. ТЕСТИ НА JWT AUTHENTICATION
    // ==========================================

    @Test
    @DisplayName("JWT Success: Токен з ROLE_ADMIN дозволяє доступ")
    void jwt_validAdminToken_allowsAccess() throws Exception {
        // Симулюємо JWT з роллю ADMIN (це перевіряє роботу JwtAuthenticationConverter)
        mockMvc.perform(post(PROTECTED_ENDPOINT)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DUMMY_CATEGORY_JSON))
                .andExpect(status().isCreated()); // Очікуємо 201
    }

    @Test
    @DisplayName("JWT Fail: Токен з ROLE_USER отримує 403 Forbidden")
    void jwt_invalidUserToken_returnsForbidden() throws Exception {
        // Симулюємо JWT з роллю USER, яка не має прав на POST
        mockMvc.perform(post(PROTECTED_ENDPOINT)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DUMMY_CATEGORY_JSON))
                .andExpect(status().isForbidden()); // Очікуємо 403
    }

    // ==========================================
    // 2. ТЕСТИ НА API KEY AUTHENTICATION
    // ==========================================

    @Test
    @DisplayName("API Key Success: Валідний ключ BOT дозволяє доступ (201)")
    void apiKey_validBotKey_allowsAccess() throws Exception {
        // Перевірка роботи ApiKeyAuthenticationFilter
        mockMvc.perform(post(PROTECTED_ENDPOINT)
                        .header("X-Api-Key", VALID_BOT_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DUMMY_CATEGORY_JSON))
                .andExpect(status().isCreated()); // Очікуємо 201
    }

    @Test
    @DisplayName("API Key Fail: Неправильний ключ отримує 401 Unauthorized")
    void apiKey_invalidKey_returnsUnauthorized() throws Exception {
        // Фільтр повинен пропустити далі, де відсутність автентифікації призведе до 401
        mockMvc.perform(post(PROTECTED_ENDPOINT)
                        .header("X-Api-Key", "bad-key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DUMMY_CATEGORY_JSON))
                .andExpect(status().isUnauthorized()); // Очікуємо 401
    }

    @Test
    @DisplayName("JWT та API Key Fail: Відсутній ключ отримує 401 Unauthorized")
    void apiKey_missingKey_returnsUnauthorized() throws Exception {
        // Жодного хедера аутентифікації
        mockMvc.perform(post(PROTECTED_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DUMMY_CATEGORY_JSON))
                .andExpect(status().isUnauthorized()); // Очікуємо 401
    }
}