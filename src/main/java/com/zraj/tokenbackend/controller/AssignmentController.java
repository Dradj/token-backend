package com.zraj.tokenbackend.controller;

import com.zraj.tokenbackend.RFC5987;
import com.zraj.tokenbackend.dto.AssignmentDTO;
import com.zraj.tokenbackend.dto.teacher.SubmittedAssignmentDTO;
import com.zraj.tokenbackend.dto.teacher.TeacherAssignmentDTO;
import com.zraj.tokenbackend.entity.AssignmentMaterial;
import com.zraj.tokenbackend.entity.AssignmentSubmission;
import com.zraj.tokenbackend.service.AssignmentFileService;
import com.zraj.tokenbackend.service.GradeService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentFileService fileService;
    private final GradeService gradeService;
    private final UserService userService;
    private final AssignmentFileService assignmentFileService;
    private final TeacherService teacherService;

    public AssignmentController(AssignmentFileService fileService, GradeService gradeService, UserService userService, AssignmentFileService assignmentFileService, TeacherService teacherService) {
        this.fileService = fileService;
        this.gradeService = gradeService;
        this.userService = userService;
        this.assignmentFileService = assignmentFileService;
        this.teacherService = teacherService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable Long id) {
        AssignmentDTO assignmentDTO = assignmentFileService.getAssignmentById(id);
        return ResponseEntity.ok(assignmentDTO);
    }

    @GetMapping("/{id}/materials")
    public List<AssignmentMaterial> getMaterials(@PathVariable Long id) {
        return fileService.listMaterials(id);
    }

    /*@GetMapping("/files/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String type,
            @PathVariable String filename
    ) throws Exception {
        Resource file = fileService.loadAsResource(type, filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
    */
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

                    return new SubmittedAssignmentDTO(
                            submission.getId(),               // submissionId
                            submission.getAssignment().getTitle(),  // assignmentTitle
                            downloadUrl,                       // downloadUrl
                            submission.getFileName(),          // fileName
                            submission.getSubmittedAt()        // submittedAt
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
    @PostMapping("/{id}/materials")
    public AssignmentMaterial uploadMaterial(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file

    ) throws Exception {
        // Проверить роль преподавателя через auth
        return fileService.storeMaterial(id, file);
    }

    @GetMapping("/{id}/submissions/forteacher")
    public List<AssignmentSubmission> getAllStudentsSubmissions(
            @PathVariable Long id

    ) {
        return fileService.listAllStudentsSubmissions(id);
    }

    @PostMapping("/{id}/grade")
    public ResponseEntity<?> gradeSubmission(
            @PathVariable Long id,
            @RequestParam Long studentId,
            @RequestParam BigDecimal grade,
            Authentication auth
    ) {
        // Проверить роль преподавателя
        gradeService.saveOrUpdateGrade(id, studentId, grade);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/teacher")
    public ResponseEntity<List<TeacherAssignmentDTO>> getTeacherAssignments(Authentication auth) {
        String email = auth.getName();
        Long teacherId = userService.getUserIdByEmail(email);
        List<TeacherAssignmentDTO> result = teacherService.getTeacherAssignments(teacherId);
        return ResponseEntity.ok(result);
    }

}
