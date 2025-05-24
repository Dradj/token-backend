package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseRepository extends JpaRepository<Course, Long> {
}
