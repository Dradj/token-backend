package com.zraj.tokenbackend.service;

import com.zraj.tokenbackend.RFC5987;
import com.zraj.tokenbackend.dto.AssignmentDTO;
import com.zraj.tokenbackend.dto.teacher.SubmittedAssignmentDTO;
import com.zraj.tokenbackend.entity.*;
import com.zraj.tokenbackend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;


@Service
public class AssignmentFileService {

    private final Path rootLocation;
    private final AssignmentMaterialRepository materialRepo;
    private final AssignmentSubmissionRepository submissionRepo;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final AssignmentGradeRepository gradeRepo;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;

    public AssignmentFileService(
            @Value("${app.upload.dir}") String uploadDir,
            AssignmentMaterialRepository materialRepo,
            AssignmentSubmissionRepository submissionRepo,
            AssignmentRepository assignmentRepository, StudentRepository studentRepository, AssignmentGradeRepository gradeRepo, AssignmentSubmissionRepository assignmentSubmissionRepository) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.materialRepo = materialRepo;
        this.submissionRepo = submissionRepo;
        this.assignmentRepository = assignmentRepository;
        this.studentRepository = studentRepository;
        this.gradeRepo = gradeRepo;
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
    }

    public List<AssignmentMaterial> listMaterials(Long assignmentId) {
        return materialRepo.findByAssignmentId(assignmentId);
    }

    public AssignmentDTO getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .map(assignment -> new AssignmentDTO(
                        assignment.getId(),
                        assignment.getTitle(),
                        assignment.getDescription(),
                        assignment.getDueDate()
                ))
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + id));
    }

    public Resource loadAsResource(String type, String encodedFilename) throws Exception {

        String filename = URLDecoder.decode(encodedFilename, StandardCharsets.UTF_8);
        Path file = rootLocation.resolve(type).resolve(filename).normalize();
        if (!Files.probeContentType(file).equals("application/pdf")) {
            throw new RuntimeException("Invalid file type");
        }
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        }
        throw new RuntimeException("Не удалось прочитать файл: " + filename);
    }


    public AssignmentMaterial storeMaterial(Long assignmentId, MultipartFile file) throws Exception {
        // 1. Проверяем и создаем директории
        Path materialsDir = rootLocation.resolve("materials");
        Files.createDirectories(materialsDir);

        String filename = file.getOriginalFilename();
        assert filename != null;

        // 3. Сохраняем файл
        String relativePath = "materials" + "/" + file.getOriginalFilename();
        Path dest = rootLocation.resolve(relativePath).normalize();

        // 4. Проверка безопасности пути
        if (!dest.startsWith(rootLocation)) {
            throw new AccessDeniedException("Попытка сохранить файл вне целевой директории");
        }

        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

        // 5. Сохраняем метаданные в БД
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));


        AssignmentMaterial material = new AssignmentMaterial();
        material.setAssignment(assignment);
        material.setFileName(filename);
        material.setFilePath(relativePath);

        return materialRepo.save(material);
    }

    //public List<AssignmentSubmission> listAllStudentsSubmissions(Long assignmentId) {
    //    return submissionRepo.findByAssignmentId(assignmentId);
    //}


    @Transactional
    public AssignmentSubmission storeSubmission(Long assignmentId, Long studentId, MultipartFile file) throws Exception {
        // 1. Проверяем и создаем директории
        Path submissionsDir = rootLocation.resolve("submissions");
        Files.createDirectories(submissionsDir);

        String filename = file.getOriginalFilename();
        assert filename != null;

        // 3. Сохраняем файл
        String relativePath = "submissions" + "/" + file.getOriginalFilename();
        Path dest = rootLocation.resolve(relativePath).normalize();

        // 4. Проверка безопасности пути
        if (!dest.startsWith(rootLocation)) {
            throw new AccessDeniedException("Попытка сохранить файл вне целевой директории");
        }

        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

        // 5. Сохраняем метаданные в БД
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setFileName(filename);
        submission.setFilePath(relativePath);

        return submissionRepo.save(submission);
    }

    public List<SubmittedAssignmentDTO> getSubmittedAssignmentsWithGrades(Long studentId) {
        return assignmentSubmissionRepository.findAllByStudentUserId(studentId).stream()
                .map(submission -> {
                    Long assignmentId = submission.getAssignment().getId();
                    AssignmentGradeId gradeId = new AssignmentGradeId(studentId, assignmentId);

                    Optional<AssignmentGrade> gradeOpt = gradeRepo.findById(gradeId);

                    BigDecimal gradeValue = gradeOpt.map(AssignmentGrade::getGrade).orElse(null);
                    Boolean rewarded = gradeOpt.map(AssignmentGrade::isRewarded).orElse(false);

                    String encodedFileName = RFC5987.encode(submission.getFileName(), StandardCharsets.UTF_8);
                    String downloadUrl = "http://localhost:8080/api/assignments/files/submissions/" + encodedFileName;

                    return new SubmittedAssignmentDTO(
                            submission.getId(),
                            assignmentId,
                            downloadUrl,
                            submission.getFileName(),
                            submission.getSubmittedAt(),
                            gradeValue,
                            rewarded
                    );
                })
                .toList();
    }

    public List<AssignmentSubmission> getStudentSubmissions(
            Long assignmentId,
            Long studentId
    ) {
        return submissionRepo.findAllByAssignment_IdAndStudent_UserId(assignmentId, studentId);
    }

    @Transactional
    public void deleteFile(Long fileId) throws IOException {
        // Проверяем, существует ли файл в MaterialRepository
        Optional<AssignmentMaterial> materialOpt = materialRepo.findById(fileId);
        if (materialOpt.isPresent()) {
            AssignmentMaterial material = materialOpt.get();
            Path filePath = rootLocation.resolve(material.getFilePath()).normalize();
            Files.delete(filePath);
            materialRepo.delete(material);
            return;
        }

        // Если не найден в Material, проверяем AssignmentSubmissionRepository
        Optional<AssignmentSubmission> submissionOpt = submissionRepo.findById(fileId);
        if (submissionOpt.isPresent()) {
            AssignmentSubmission submission = submissionOpt.get();
            Path filePath = rootLocation.resolve(submission.getFilePath()).normalize();
            Files.delete(filePath);
            submissionRepo.delete(submission);
            return;
        }

        // Если файл не найден ни в одном репозитории
        throw new FileNotFoundException("Файл с ID " + fileId + " не найден");
    }


}

