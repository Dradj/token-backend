package com.zraj.tokenbackend.service;

import com.zraj.tokenbackend.entity.AssignmentGrade;
import com.zraj.tokenbackend.entity.AssignmentGradeId;
import com.zraj.tokenbackend.repository.AssignmentGradeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GradeServiceImpl implements GradeService {

    private final AssignmentGradeRepository gradeRepo;

    public GradeServiceImpl(AssignmentGradeRepository gradeRepo) {
        this.gradeRepo = gradeRepo;
    }

    @Override
    @Transactional
    public void saveOrUpdateGrade(Long assignmentId, Long studentId, BigDecimal grade) {
        // Создаем составной ключ для поиска
        AssignmentGradeId gradeId = new AssignmentGradeId();
        gradeId.setStudentId(studentId);
        gradeId.setAssignmentId(assignmentId);

        gradeRepo.findById(gradeId)
                .map(existing -> {
                    existing.setGrade(grade);
                    return gradeRepo.save(existing);
                })
                .orElseGet(() -> {
                    AssignmentGrade ag = new AssignmentGrade(gradeId, grade); // Передаем ключ и оценку
                    return gradeRepo.save(ag);
                });
    }


}