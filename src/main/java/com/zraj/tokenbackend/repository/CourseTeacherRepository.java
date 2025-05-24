package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.Course;
import com.zraj.tokenbackend.entity.CourseTeacher;
import com.zraj.tokenbackend.entity.CourseTeacherId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseTeacherRepository extends JpaRepository<CourseTeacher, CourseTeacherId> {
    @Query("SELECT ct.course FROM CourseTeacher ct WHERE ct.teacher.userId = :teacherId")
    List<Course> findCoursesByTeacherId(@Param("teacherId") Long teacherId);
}
