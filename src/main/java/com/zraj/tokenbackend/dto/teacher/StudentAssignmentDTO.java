package com.zraj.tokenbackend.dto.teacher;

import java.util.List;

public record StudentAssignmentDTO(
        Long studentId,
        String studentName,
        List<SubmittedAssignmentDTO> assignments
) {}
