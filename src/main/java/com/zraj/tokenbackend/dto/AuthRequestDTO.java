package com.zraj.tokenbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequestDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    // Геттеры и сеттеры
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Конструкторы
    public AuthRequestDTO() {
    }

    public AuthRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
