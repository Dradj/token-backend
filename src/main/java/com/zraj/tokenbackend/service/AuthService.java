package com.zraj.tokenbackend.service;


import com.zraj.tokenbackend.dto.AuthResponseDTO;
import com.zraj.tokenbackend.dto.LoginResponseDTO;
import com.zraj.tokenbackend.security.jwt.CustomUserDetailsService;
import com.zraj.tokenbackend.security.jwt.JwtUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;



import java.util.Arrays;


@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    public AuthService(
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtUtils jwtUtils
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    public LoginResponseDTO authenticate(String email, String password, HttpServletResponse response) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        setRefreshTokenCookie(response, refreshToken);

        String role = userDetails.getAuthorities().toString();
        return new LoginResponseDTO(accessToken, role);
    }

    public AuthResponseDTO refreshAccessToken(HttpServletRequest request) {
        System.out.println("[AuthService] Запрос на обновление access токена");

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            System.out.println("[AuthService] Нет cookies в запросе");
            throw new RuntimeException("No cookies found");
        }

        for (Cookie cookie : cookies) {
            System.out.println("[AuthService] Cookie: " + cookie.getName() + "=" + cookie.getValue());
        }

        String refreshToken = Arrays.stream(cookies)
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken == null) {
            System.out.println("[AuthService] Refresh token не найден среди cookies");
            throw new RuntimeException("Refresh token not found");
        }

        System.out.println("[AuthService] Найден refresh токен: " + refreshToken);

        if (!jwtUtils.validateToken(refreshToken)) {
            System.out.println("[AuthService] Refresh token невалидный");
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtUtils.extractEmail(refreshToken);
        System.out.println("[AuthService] Email из refresh токена: " + email);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String newAccessToken = jwtUtils.generateAccessToken(userDetails);
        System.out.println("[AuthService] Сгенерирован новый access token");

        return new AuthResponseDTO(newAccessToken);
    }

    public boolean validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return false;
        }
        return jwtUtils.validateToken(refreshToken);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 дней
        response.addCookie(cookie);
    }
}


