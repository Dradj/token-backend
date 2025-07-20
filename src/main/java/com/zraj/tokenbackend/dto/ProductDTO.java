package com.zraj.tokenbackend.dto;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        String name,
        String imageUrl,
        BigDecimal price
) {}
