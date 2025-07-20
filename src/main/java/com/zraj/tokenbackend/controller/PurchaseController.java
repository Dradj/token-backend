package com.zraj.tokenbackend.controller;

import com.zraj.tokenbackend.entity.StudentPurchase;
import com.zraj.tokenbackend.repository.StudentPurchaseRepository;
import com.zraj.tokenbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final StudentPurchaseRepository repo;
    private final UserService userService;

    public PurchaseController(StudentPurchaseRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    // 1) получить список купленных product_id для данного студента
    @GetMapping("/get")
    public List<Long> getPurchased(Authentication auth) {
        String email = auth.getName();
        Long studentId = userService.getUserIdByEmail(email);
        return repo.findAllByStudentId(studentId)
                .stream()
                .map(StudentPurchase::getProductId)
                .toList();
    }

    // 2) сохранить новую покупку
    @PostMapping("/buy")
    public ResponseEntity<Void> purchase(
            Authentication auth,
            @RequestParam Long productId) {
        String email = auth.getName();
        Long studentId = userService.getUserIdByEmail(email);
        if (!repo.existsByStudentIdAndProductId(studentId, productId)) {
            StudentPurchase sp = new StudentPurchase();
            sp.setStudentId(studentId);
            sp.setProductId(productId);
            repo.save(sp);
        }
        return ResponseEntity.ok().build();
    }
}
