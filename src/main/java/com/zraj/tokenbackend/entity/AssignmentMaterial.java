package com.zraj.tokenbackend.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "assignment_materials")
public class AssignmentMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt = Instant.now();

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Long getAssignmentId() {return assignmentId;}
    public void setAssignmentId(Long assignmentId) {this.assignmentId = assignmentId;}
    public String getFileName() {return fileName;}
    public void setFileName(String fileName) {this.fileName = fileName;}
    public String getFilePath() {return filePath;}
    public void setFilePath(String filePath) {this.filePath = filePath;}
    public Instant getUploadedAt() {return uploadedAt;}
    public void setUploadedAt(Instant uploadedAt) {this.uploadedAt = uploadedAt;}
}
