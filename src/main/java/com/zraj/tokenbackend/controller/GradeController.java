package com.zraj.tokenbackend.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zraj.tokenbackend.dto.GradeDTO;
import com.zraj.tokenbackend.dto.RewardRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.zraj.tokenbackend.entity.AssignmentGrade;
import com.zraj.tokenbackend.entity.AssignmentGradeId;
import com.zraj.tokenbackend.redis.WalletService;
import com.zraj.tokenbackend.repository.AssignmentGradeRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
public class GradeController {

    private final AssignmentGradeRepository assignmentGradeRepository;
    private final WalletService walletService;
    private final RestTemplate restTemplate;

    public GradeController(AssignmentGradeRepository assignmentGradeRepository, WalletService walletService, RestTemplate restTemplate) {
        this.assignmentGradeRepository = assignmentGradeRepository;
        this.walletService = walletService;
        this.restTemplate = restTemplate;
    }


    @PostMapping("/{assignmentId}/grade")
    public ResponseEntity<Map<String, String>> saveGrade(
            @PathVariable Long assignmentId,
            @RequestBody GradeDTO dto,
            HttpServletRequest httpRequest) {

        Long studentId = dto.getStudentId();
        BigDecimal gradeValue = dto.getGrade();
        AssignmentGradeId id = new AssignmentGradeId(studentId, assignmentId);

        // Находим или создаём запись оценки
        AssignmentGrade grade = assignmentGradeRepository.findById(id)
                .orElse(new AssignmentGrade(id, gradeValue));
        grade.setGrade(gradeValue);

        StringBuilder message = new StringBuilder("Оценка сохранена. ");

        // Проверяем кошелёк студента
        String walletAddress = walletService.getStudentWallet(studentId);
        if (walletAddress != null) {
            try {
                // Создаём DTO-запрос
                RewardRequestDTO request = new RewardRequestDTO(walletAddress, gradeValue);

                // Отладочный вывод JSON тела запроса
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(request);
                System.out.println("Запрос в transfer-service: " + json);

                // Заголовки запроса
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Пробрасываем JWT из исходного запроса
                String authHeader = httpRequest.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    headers.set("Authorization", authHeader);
                }

                HttpEntity<RewardRequestDTO> entity = new HttpEntity<>(request, headers);

                // Отправляем POST-запрос в transfer-service
                ResponseEntity<Void> response = restTemplate.postForEntity(
                        "http://localhost:8000/transfer",
                        entity,
                        Void.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    grade.setRewarded(true);
                    message.append("Токены успешно отправлены.");
                } else {
                    message.append("Не удалось отправить токены (код: ")
                            .append(response.getStatusCode().value()).append(").");
                }

            } catch (Exception e) {
                message.append("Ошибка при отправке токенов: ").append(e.getMessage());
            }
        } else {
            message.append("Кошелёк студента не найден.");
        }

        assignmentGradeRepository.save(grade);

        // Возвращаем JSON с сообщением
        Map<String, String> responseBody = Collections.singletonMap("message", message.toString());
        return ResponseEntity.ok(responseBody);
    }

}
