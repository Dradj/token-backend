package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.AssignmentMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentMaterialRepository extends JpaRepository<AssignmentMaterial, Long> {
    List<AssignmentMaterial> findByAssignmentId(Long assignmentId);
}