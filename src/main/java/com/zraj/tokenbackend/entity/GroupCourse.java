package com.zraj.tokenbackend.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "group_courses")
public class GroupCourse {

    @EmbeddedId
    private GroupCourseId id;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    @JsonBackReference
    private StudentGroup group;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    public GroupCourseId getId() { return id; }
    public void setId(GroupCourseId id) { this.id = id; }
    public StudentGroup getGroup() { return group; }
    public void setGroup(StudentGroup group) { this.group = group; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}

