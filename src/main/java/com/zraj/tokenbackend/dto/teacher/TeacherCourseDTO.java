package com.zraj.tokenbackend.dto.teacher;

import java.util.List;

public record TeacherCourseDTO(
        Long courseId,
        String courseName,
        List<GroupAssignmentDTO> groups
) {}