package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.StudentPurchase;
import com.zraj.tokenbackend.entity.StudentPurchaseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentPurchaseRepository extends JpaRepository<StudentPurchase, StudentPurchaseId> {
    List<StudentPurchase> findAllByStudentId(Long studentId);
    boolean existsByStudentIdAndProductId(Long studentId, Long productId);
}
