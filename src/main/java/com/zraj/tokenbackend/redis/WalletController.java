package com.zraj.tokenbackend.redis;

import com.zraj.tokenbackend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final RedisCodeService redisCodeService;
    private final JavaMailSender mailSender;
    private final UserService userService;
    private final WalletService walletService;

    @Value("${app.verification.code-length}")
    private int codeLength;

    public WalletController(RedisCodeService redisCodeService, JavaMailSender mailSender, UserService userService, WalletService walletService) {
        this.redisCodeService = redisCodeService;
        this.mailSender = mailSender;
        this.userService = userService;
        this.walletService = walletService;
    }

    @PostMapping("/initiate-change")
    public void initiateChange(
            @RequestBody WalletAddressDTO request,
            Authentication authentication
    ) {
        String newWallet = request.walletAddress();
        String code = generateVerificationCode();

        // Сохраняем в Redis
        redisCodeService.saveRequest(
                authentication.getName(),
                new WalletChangeRequestDTO(newWallet, code)
        );

        // Отправляем email
        sendVerificationEmail(authentication.getName(), code);
    }

    @PostMapping("/confirm-change")
    public void confirmChange(
            @RequestBody Map<String, String> body,
            Authentication authentication
    ) {
        String code = body.get("code");

        WalletChangeRequestDTO request = redisCodeService.getRequest(authentication.getName());

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Код подтверждения истек или не существует"
            );
        }

        if (!request.code().equals(code)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Неверный код подтверждения"
            );
        }

        // Очищаем Redis
        redisCodeService.deleteRequest(authentication.getName());
    }

    private String generateVerificationCode() {
        int max = (int) Math.pow(10, codeLength) - 1;
        return String.format("%0" + codeLength + "d", ThreadLocalRandom.current().nextInt(max));
    }

    private void sendVerificationEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Подтверждение смены кошелька");
        message.setText(
                "Ваш код подтверждения: " + code + "\n" +
                        "Код действителен в течение 10 минут.\n" +
                        "Никому не сообщайте этот код."
        );
        mailSender.send(message);
    }



    @PostMapping("/set")
    public ResponseEntity<?> setWalletAddress(@RequestBody WalletAddressDTO request, Authentication authentication) {

        String email = authentication.getName();
        Long id = userService.getUserIdByEmail(email);
        walletService.setStudentWallet(id, request.walletAddress());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeWalletAddress(Authentication authentication) {
        String email = authentication.getName();
        Long id = userService.getUserIdByEmail(email);

        walletService.removeStudentWallet(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get")
    public ResponseEntity<WalletAddressDTO> getWalletAddress(Authentication authentication) {
        String email = authentication.getName();
        Long id = userService.getUserIdByEmail(email);

        String walletAddress = walletService.getStudentWallet(id);
        return ResponseEntity.ok(new WalletAddressDTO(walletAddress));
    }
}


