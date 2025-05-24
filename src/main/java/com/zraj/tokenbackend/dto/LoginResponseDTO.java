package com.zraj.tokenbackend.dto;

public class LoginResponseDTO {
    private String accessToken;
    private String role;

    public LoginResponseDTO(String accessToken,String role) {
        this.accessToken = accessToken;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccessToken() { return accessToken; }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
