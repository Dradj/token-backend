package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    // Дополнительные методы при необходимости
}
