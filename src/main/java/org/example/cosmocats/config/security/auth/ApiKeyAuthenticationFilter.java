package org.example.cosmocats.config.security.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-Api-Key";

    @Value("${app.security.bot-key}")
    private String botKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestApiKey = request.getHeader(API_KEY_HEADER);

        if (requestApiKey != null && botKey.equals(requestApiKey)) {
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_BOT"));
            var authentication = new UsernamePasswordAuthenticationToken(
                    "BotService", null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Якщо ключа немає або він неправильний — просто йдемо далі.
        // Якщо це був юзер без токена — його відіш'є Spring Security пізніше.
        // Це дозволяє працювати і JWT (Authorization: Bearer ...), і API Key паралельно.
        filterChain.doFilter(request, response);
    }
}