package com.zraj.tokenbackend.redis;

import com.zraj.tokenbackend.entity.Student;
import com.zraj.tokenbackend.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final StudentRepository studentRepository;

    public WalletService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public void setStudentWallet(Long userId, String walletAddress) {
        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        // Проверка формата адреса
        if (!isValidEthAddress(walletAddress)) {
            throw new IllegalArgumentException("Invalid Ethereum address format");
        }

        student.setWalletAddress(walletAddress);
        studentRepository.save(student);
    }

    @Transactional
    public void removeStudentWallet(Long userId) {
        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        student.setWalletAddress(null);
        studentRepository.save(student);
    }

    private boolean isValidEthAddress(String address) {
        return address != null && address.matches("^0x[a-fA-F0-9]{40}$");
    }

    @Transactional
    public String getStudentWallet(Long userId) {
        return studentRepository.findByUserId(userId)
                .map(Student::getWalletAddress)
                .orElse(null);
    }
}
