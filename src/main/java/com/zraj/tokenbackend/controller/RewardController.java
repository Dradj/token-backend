package com.zraj.tokenbackend.controller;

import com.zraj.tokenbackend.dto.GradeDTO;
import com.zraj.tokenbackend.dto.RewardRequestDTO;
import com.zraj.tokenbackend.entity.AssignmentGrade;
import com.zraj.tokenbackend.entity.AssignmentGradeId;
import com.zraj.tokenbackend.redis.WalletService;
import com.zraj.tokenbackend.repository.AssignmentGradeRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reward")
public class RewardController {

    private final AssignmentGradeRepository assignmentGradeRepository;
    private final WalletService walletService;
    private final RestTemplate restTemplate;

    public RewardController(AssignmentGradeRepository assignmentGradeRepository, WalletService walletService, RestTemplate restTemplate) {
        this.assignmentGradeRepository = assignmentGradeRepository;
        this.walletService = walletService;
        this.restTemplate = restTemplate;
    }


    @PostMapping("/{assignmentId}")
    public ResponseEntity<Map<String, String>> rewardStudent(
            @PathVariable Long assignmentId,
            @RequestBody GradeDTO dto,
            HttpServletRequest httpRequest
    ) {
        Long studentId = dto.getStudentId();
        BigDecimal gradeValue = dto.getGrade();
        AssignmentGradeId id = new AssignmentGradeId(studentId, assignmentId);

        Optional<AssignmentGrade> optionalGrade = assignmentGradeRepository.findById(id);

        if (optionalGrade.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Оценка не найдена"));
        }

        AssignmentGrade grade = optionalGrade.get();

        if (grade.getGrade() == null || grade.getGrade().compareTo(gradeValue) != 0) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Оценка не совпадает или отсутствует"));
        }

        if (grade.isRewarded()) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Награда уже получена"));
        }

        String walletAddress = walletService.getStudentWallet(studentId);
        if (walletAddress == null) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Кошелёк студента не найден"));
        }

        try {
            RewardRequestDTO request = new RewardRequestDTO(walletAddress, gradeValue);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                headers.set("Authorization", authHeader);
            }

            HttpEntity<RewardRequestDTO> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Void> transferResponse = restTemplate.postForEntity(
                    "http://localhost:8000/transfer",
                    entity,
                    Void.class
            );

            if (transferResponse.getStatusCode().is2xxSuccessful()) {
                grade.setRewarded(true);
                assignmentGradeRepository.save(grade);

                return ResponseEntity.ok(Collections.singletonMap("message", "Токены успешно отправлены"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(Collections.singletonMap("message",
                                "Не удалось отправить токены (код: " + transferResponse.getStatusCode().value() + ")"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Ошибка при отправке токенов: " + e.getMessage()));
        }
    }
}
