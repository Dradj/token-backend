package com.zraj.tokenbackend.dto.teacher;

import java.math.BigDecimal;
import java.time.Instant;

public record SubmittedAssignmentDTO(
        Long submissionId,
        Long assignmentId,
        String downloadUrl,
        String fileName,
        Instant submittedAt,
        BigDecimal gradeValue,
        Boolean rewarded
) {}