package com.zraj.tokenbackend.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CourseTeacherId implements Serializable {

    private Long courseId;
    private Long teacherId;

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseTeacherId that)) return false;
        return Objects.equals(courseId, that.courseId) &&
                Objects.equals(teacherId, that.teacherId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, teacherId);
    }
}

