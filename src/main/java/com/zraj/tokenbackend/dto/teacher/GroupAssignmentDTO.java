package com.zraj.tokenbackend.dto.teacher;

import java.util.List;

public record GroupAssignmentDTO(
        Long groupId,
        String groupName,
        List<StudentAssignmentDTO> students
) {}
