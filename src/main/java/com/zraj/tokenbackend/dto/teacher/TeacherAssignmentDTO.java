package com.zraj.tokenbackend.dto.teacher;

import java.util.List;

public record TeacherAssignmentDTO(
        Long courseId,
        String courseName,
        List<GroupAssignmentDTO> groups
) {}