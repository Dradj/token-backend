package com.zraj.tokenbackend.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        if ("http://localhost:4200/api/auth/refresh".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 1. Извлечение токена из заголовка
        String authHeader = request.getHeader("Authorization");

        // 2. Если токена нет или он не начинается с "Bearer " — пропускаем запрос
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Извлекаем токен (убираем "Bearer ")
        String jwt = authHeader.substring(7);

        // 4. Валидация токена
        if (jwtUtils.validateToken(jwt)) {
            // 5. Извлекаем email из токена
            String email = jwtUtils.extractEmail(jwt);

            // 6. Загружаем данные пользователя
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 7. Создаем объект аутентификации
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // credentials (пароль не нужен)
                    userDetails.getAuthorities()
            );

            // 8. Устанавливаем аутентификацию в контекст
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 9. Передаем запрос дальше по цепочке фильтров
        filterChain.doFilter(request, response);
    }
}
