package com.zraj.tokenbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "student_groups")
public class StudentGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",nullable = false)
    private String name;

    @OneToMany(mappedBy = "group")
    @JsonManagedReference
    private List<GroupCourse> groupCourses;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<GroupCourse> getGroupCourses() { return groupCourses; }
    public void setGroupCourses(List<GroupCourse> groupCourses) { this.groupCourses = groupCourses; }
}

