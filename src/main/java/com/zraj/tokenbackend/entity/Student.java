package com.zraj.tokenbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    private Long userId; // Совпадает с id пользователя

    @OneToOne
    @MapsId // Связывает userId с id User
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private StudentGroup group;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public StudentGroup getGroup() { return group; }
    public void setGroup(StudentGroup group) { this.group = group; }
}