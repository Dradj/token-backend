package com.zraj.tokenbackend.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "assignment_materials")
public class AssignmentMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "uploaded_at", nullable = false)
    private Instant submittedAt = Instant.now();

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Assignment getAssignment() {return assignment;}
    public void setAssignment(Assignment assignment) {this.assignment = assignment;}
    public String getFileName() {return fileName;}
    public void setFileName(String fileName) {this.fileName = fileName;}
    public String getFilePath() {return filePath;}
    public void setFilePath(String filePath) {this.filePath = filePath;}
    public Instant getSubmittedAt() {return submittedAt;}
    public void setSubmittedAt(Instant submittedAt) {this.submittedAt = submittedAt;}
}
