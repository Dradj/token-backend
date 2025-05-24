package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.GroupCourse;
import com.zraj.tokenbackend.entity.GroupCourseId;
import com.zraj.tokenbackend.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupCourseRepository extends JpaRepository<GroupCourse, GroupCourseId> {

    List<GroupCourse> findByGroupId(Long group_id);

    @Query("SELECT gc.group FROM GroupCourse gc WHERE gc.course.id = :courseId")
    List<StudentGroup> findGroupsByCourseId(@Param("courseId") Long courseId);
}
