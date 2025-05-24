package com.zraj.tokenbackend.service;

import java.math.BigDecimal;

public interface GradeService {
    void saveOrUpdateGrade(Long assignmentId, Long studentId, BigDecimal grade);
}
