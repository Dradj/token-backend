package com.zraj.tokenbackend.repository;


import com.zraj.tokenbackend.entity.Assignment;
import com.zraj.tokenbackend.entity.AssignmentSubmission;
import com.zraj.tokenbackend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);
    List<AssignmentSubmission> findAllByAssignment_IdAndStudent_UserId(Long assignmentId, Long studentId);


    @Query("SELECT s FROM AssignmentSubmission s " +
            "JOIN s.assignment a " +
            "JOIN a.course c " +
            "JOIN s.student st " +
            "JOIN st.group g " +
            "WHERE c.id = :courseId AND g.id = :groupId")
    List<AssignmentSubmission> findByCourseAndGroup(
            @Param("courseId") Long courseId,
            @Param("groupId") Long groupId
    );
}