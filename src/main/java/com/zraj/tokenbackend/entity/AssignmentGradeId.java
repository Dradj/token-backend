package com.zraj.tokenbackend.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AssignmentGradeId implements Serializable {

    private Long studentId;
    private Long assignmentId;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssignmentGradeId that)) return false;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(assignmentId, that.assignmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, assignmentId);
    }
}

