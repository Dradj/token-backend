package com.zraj.tokenbackend.controller;

import com.zraj.tokenbackend.dto.AssignmentDTO;
import com.zraj.tokenbackend.entity.Course;
import com.zraj.tokenbackend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final StudentService studentService;

    @Autowired
    public CourseController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/group/{groupId}")
    public List<Course> getCoursesForGroup(@PathVariable Long groupId) {
        System.out.println("Получен запрос на курсы группы: " + groupId);
        return studentService.getCoursesByGroupId(groupId);
    }

    @GetMapping("/{courseId}/assignments")
    public List<AssignmentDTO> getAssignmentsByCourseId(@PathVariable Long courseId) {
        return studentService.getAssignmentsByCourseId(courseId);
    }
}


