package com.zraj.tokenbackend.controller;

import com.zraj.tokenbackend.dto.AuthRequestDTO;
import com.zraj.tokenbackend.dto.AuthResponseDTO;
import com.zraj.tokenbackend.dto.LoginResponseDTO;
import com.zraj.tokenbackend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody AuthRequestDTO authRequest,
            HttpServletResponse response
    ) {
        LoginResponseDTO loginResponse = authService.authenticate(
                authRequest.getEmail(),
                authRequest.getPassword(),
                response
        );
        return ResponseEntity.ok(loginResponse);
    }


    @GetMapping("/check-session")
    public ResponseEntity<Void> checkSession(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        boolean isValid = authService.validateRefreshToken(refreshToken);

        if (!isValid) {
            // Очищаем куку, если токен невалиден
            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        return isValid
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(HttpServletRequest request) {
        AuthResponseDTO newToken = authService.refreshAccessToken(request);
        return ResponseEntity.ok(newToken);
    }
}

