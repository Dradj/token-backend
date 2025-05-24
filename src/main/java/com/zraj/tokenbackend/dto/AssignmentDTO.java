package com.zraj.tokenbackend.dto;

import java.time.LocalDate;

public record AssignmentDTO(
        Long id,
        String title,
        String description,
        LocalDate dueDate
) {}

