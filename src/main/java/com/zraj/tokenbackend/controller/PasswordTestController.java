package com.zraj.tokenbackend.controller;

import com.zraj.tokenbackend.dto.UserProfileDTO;
import com.zraj.tokenbackend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
public class PasswordTestController {

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public PasswordTestController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/test-pass")
    public String testPasswordMatching() {
        // Тестовые данные (совпадают с вашей БД)
        String rawPassword = "studentpass";
        String storedHash = "$2a$10$sFbaIQuNO1AN8Hcolyho9elsCY.17UX555/jIyunAnXKvvl1VucFC";

        boolean matches = passwordEncoder.matches(rawPassword, storedHash);

        return String.format("""
            Password test results:
            Raw password: %s
            Stored hash: %s
            Matches: %s
            PasswordEncoder: %s
            """,
                rawPassword,
                storedHash,
                matches,
                passwordEncoder.getClass().getName());
    }

    @GetMapping("/gen-hash")
    public String generateHash(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
    @GetMapping("/test")
    public void test(Authentication authentication) {
        System.out.println("Auth class: " + authentication.getClass());
        System.out.println("Auth name: " + authentication.getName());
        System.out.println("Is authenticated: " + authentication.isAuthenticated());
        System.out.println("Authorities: " + authentication.getAuthorities());


        UserProfileDTO userDto = userService.getUserProfileByEmail(authentication.getName());
        System.out.println(userDto.getEmail());
    }

}