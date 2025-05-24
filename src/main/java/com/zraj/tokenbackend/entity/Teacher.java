package com.zraj.tokenbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String department;

    public Long getUserId() { return userId; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

}
