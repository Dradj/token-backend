package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.CourseGrade;
import com.zraj.tokenbackend.entity.CourseGradeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseGradeRepository extends JpaRepository<CourseGrade, CourseGradeId> {
}
