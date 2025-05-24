package com.zraj.tokenbackend.service;

import com.zraj.tokenbackend.entity.Student;
import com.zraj.tokenbackend.entity.User;
import com.zraj.tokenbackend.repository.StudentRepository;
import com.zraj.tokenbackend.repository.TeacherRepository;
import com.zraj.tokenbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.zraj.tokenbackend.dto.UserProfileDTO;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository; // если студент
    private final TeacherRepository teacherRepository; // если преподаватель

    public UserService(UserRepository userRepository, StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    public UserProfileDTO getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        UserProfileDTO userProfileDto = new UserProfileDTO();
        userProfileDto.setId(user.getId());
        userProfileDto.setEmail(user.getEmail());
        userProfileDto.setFirstName(user.getFirstName());
        userProfileDto.setLastName(user.getLastName());
        userProfileDto.setMiddleName(user.getMiddleName());
        userProfileDto.setRole(user.getRole().name());

        // Если студент, получаем groupId
        if (user.getRole() == User.Role.ROLE_STUDENT) {
            Student student = studentRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            if (student.getGroup() == null) {
                throw new RuntimeException("Группа у студента не задана");
            }
            userProfileDto.setGroupId(student.getGroup().getId());
        }

        return userProfileDto;
    }

    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}

