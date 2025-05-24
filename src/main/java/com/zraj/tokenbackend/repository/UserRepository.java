package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Для аутентификации
    boolean existsByEmail(String email); // Проверка уникальности email
}
