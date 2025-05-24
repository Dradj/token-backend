package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.AssignmentGrade;
import com.zraj.tokenbackend.entity.AssignmentGradeId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AssignmentGradeRepository extends JpaRepository<AssignmentGrade, AssignmentGradeId> {

}
