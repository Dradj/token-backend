package com.zraj.tokenbackend.dto;

import java.math.BigDecimal;

public class GradeDTO {

    private Long studentId;
    private BigDecimal grade;

    public GradeDTO() {}

    public GradeDTO(Long studentId, BigDecimal grade) {
        this.studentId = studentId;
        this.grade = grade;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }
}
