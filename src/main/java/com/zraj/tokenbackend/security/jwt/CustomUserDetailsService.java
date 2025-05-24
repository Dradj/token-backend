package com.zraj.tokenbackend.security.jwt;

import com.zraj.tokenbackend.entity.User;
import com.zraj.tokenbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        System.out.println("[DEBUG] Loading user: " + email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        System.out.println("[DEBUG] User found. Password hash: " + user.getPassword());
        System.out.println("[DEBUG] Role authorities: " + user.getRole().getAuthorities());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true, true, true, true,
                user.getRole().getAuthorities()
        );
    }
}
