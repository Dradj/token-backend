package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
}

