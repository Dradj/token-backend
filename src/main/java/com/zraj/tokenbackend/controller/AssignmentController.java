package com.zraj.tokenbackend.controller;

import com.zraj.tokenbackend.RFC5987;
import com.zraj.tokenbackend.dto.AssignmentDTO;
import com.zraj.tokenbackend.dto.teacher.SubmittedAssignmentDTO;
import com.zraj.tokenbackend.dto.teacher.TeacherCourseDTO;
import com.zraj.tokenbackend.entity.AssignmentGrade;
import com.zraj.tokenbackend.entity.AssignmentGradeId;
import com.zraj.tokenbackend.entity.AssignmentMaterial;
import com.zraj.tokenbackend.entity.AssignmentSubmission;
import com.zraj.tokenbackend.repository.AssignmentGradeRepository;
import com.zraj.tokenbackend.service.AssignmentFileService;
import com.zraj.tokenbackend.service.TeacherService;
import com.zraj.tokenbackend.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentFileService fileService;
    private final UserService userService;
    private final AssignmentFileService assignmentFileService;
    private final TeacherService teacherService;
    private final AssignmentGradeRepository assignmentGradeRepository;

    public AssignmentController(AssignmentFileService fileService, UserService userService, AssignmentFileService assignmentFileService, TeacherService teacherService, AssignmentGradeRepository assignmentGradeRepository) {
        this.fileService = fileService;
        this.userService = userService;
        this.assignmentFileService = assignmentFileService;
        this.teacherService = teacherService;
        this.assignmentGradeRepository = assignmentGradeRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable Long id) {
        AssignmentDTO assignmentDTO = assignmentFileService.getAssignmentById(id);
        return ResponseEntity.ok(assignmentDTO);
    }

    @GetMapping("/{id}/materials")
    public List<SubmittedAssignmentDTO> getMaterials(@PathVariable Long id) {
        return fileService.listMaterials(id)
                .stream()
                .map(material -> {
                    String encodedFileName = RFC5987.encode(material.getFileName(), StandardCharsets.UTF_8);
                    String downloadUrl = "http://localhost:8080/api/assignments/files/materials/" + encodedFileName;

                    return new SubmittedAssignmentDTO(
                            material.getId(),               // submissionId
                            material.getAssignment().getId(),  // assignmentTitle
                            downloadUrl,                       // downloadUrl
                            material.getFileName(),          // fileName
                            material.getSubmittedAt(),        // submittedAt
                            null,
                            null
                    );
                })
                .toList();
    }

    @GetMapping("/files/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String type,
            @PathVariable String filename
    ) throws Exception {
        Resource file = fileService.loadAsResource(type, filename);

        // Декодируем имя файла из URL
        String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8);

        // Кодируем только для RFC 5987 (параметр filename*)
        String rfc5987Filename = RFC5987.encode(decodedFilename, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename*=UTF-8''" + RFC5987.encode(filename, StandardCharsets.UTF_8)
                )
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(file);
    }


    // ЛОГИКА ДЛЯ СТУДЕНТОВ
    @GetMapping("/{studentId}/submissions-with-grades")
    public List<SubmittedAssignmentDTO> getStudentSubmissionsWithGrades(@PathVariable Long studentId) {
        return assignmentFileService.getSubmittedAssignmentsWithGrades(studentId);
    }

    @GetMapping("/{assignmentId}/submissions")
    public List<SubmittedAssignmentDTO> getSubmissions(
            @PathVariable Long assignmentId,
            @RequestParam Long studentId
    ) {
        return assignmentFileService.getStudentSubmissions(assignmentId, studentId)
                .stream()
                .map(submission -> {
                    String encodedFileName = RFC5987.encode(submission.getFileName(), StandardCharsets.UTF_8);
                    String downloadUrl = "http://localhost:8080/api/assignments/files/submissions/" + encodedFileName;

                    AssignmentGradeId gradeId = new AssignmentGradeId(studentId, assignmentId);
                    Optional<AssignmentGrade> gradeOpt = assignmentGradeRepository.findById(gradeId);

                    BigDecimal gradeValue = gradeOpt.map(AssignmentGrade::getGrade).orElse(null);
                    boolean rewarded = gradeOpt.map(AssignmentGrade::isRewarded).orElse(false);

                    return new SubmittedAssignmentDTO(
                            submission.getId(),
                            submission.getAssignment().getId(),
                            downloadUrl,
                            submission.getFileName(),
                            submission.getSubmittedAt(),
                            gradeValue,
                            rewarded
                    );
                })
                .toList();
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> uploadSubmission(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication auth
    ) {
        try {
            String email = auth.getName();
            Long studentId = userService.getUserIdByEmail(email);
            AssignmentSubmission submission = fileService.storeSubmission(id, studentId, file);
            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<?> deleteFile(
            @PathVariable Long fileId
    ) {


        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.ok().build();

        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Ошибка удаления файла");
        }
    }



    //ЛОГИКА ДЛЯ ПРЕПОДАВАТЕЛЕЙ
    @PostMapping("/{id}/materials/upload")
    public AssignmentMaterial uploadMaterial(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file

    ) throws Exception {
        // Проверить роль преподавателя через auth
        return fileService.storeMaterial(id, file);
    }

    //@GetMapping("/{id}/submissions/forteacher")
    //public List<AssignmentSubmission> getAllStudentsSubmissions(
    //        @PathVariable Long id

    //) {
    //    return fileService.listAllStudentsSubmissions(id);
    //}

    @GetMapping("/teacher")
    public ResponseEntity<List<TeacherCourseDTO>> getTeacherAssignments(Authentication auth) {
        String email = auth.getName();
        Long teacherId = userService.getUserIdByEmail(email);
        List<TeacherCourseDTO> result = teacherService.getTeacherAssignments(teacherId);
        return ResponseEntity.ok(result);
    }

}
