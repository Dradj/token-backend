package com.zraj.tokenbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "assignment_grades")
public class AssignmentGrade {

    @EmbeddedId
    private AssignmentGradeId id;

    @Column(nullable = false, precision = 3, scale = 1, columnDefinition = "DECIMAL(3,1)")
    private BigDecimal grade;

    // Конструкторы
    public AssignmentGrade() {}

    public AssignmentGrade(AssignmentGradeId id, BigDecimal grade) {
        this.id = id;
        this.grade = grade;
    }

    public AssignmentGradeId getId() { return id; }
    public void setId(AssignmentGradeId id) { this.id = id; }
    public BigDecimal getGrade() { return grade; }
    public void setGrade(BigDecimal grade) { this.grade = grade; }
}

