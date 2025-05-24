package com.zraj.tokenbackend.dto.teacher;

import java.time.Instant;

public record SubmittedAssignmentDTO(
        Long submissionId,
        String assignmentTitle,
        String downloadUrl,
        String fileName,
        Instant submittedAt
) {}