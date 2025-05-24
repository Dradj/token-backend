package com.zraj.tokenbackend.controller;

import com.zraj.tokenbackend.dto.UserProfileDTO;
import com.zraj.tokenbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current")
    public ResponseEntity<UserProfileDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserProfileDTO userDto = userService.getUserProfileByEmail(email);
        return ResponseEntity.ok(userDto);
    }
}
