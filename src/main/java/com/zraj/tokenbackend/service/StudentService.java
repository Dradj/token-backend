package com.zraj.tokenbackend.service;

import com.zraj.tokenbackend.dto.AssignmentDTO;
import com.zraj.tokenbackend.entity.Assignment;
import com.zraj.tokenbackend.entity.Course;
import com.zraj.tokenbackend.entity.GroupCourse;
import com.zraj.tokenbackend.repository.AssignmentRepository;
import com.zraj.tokenbackend.repository.CourseRepository;
import com.zraj.tokenbackend.repository.GroupCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final GroupCourseRepository groupCourseRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;

    @Autowired
    public StudentService(GroupCourseRepository groupCourseRepository, CourseRepository courseRepository, AssignmentRepository assignmentRepository) {
        this.groupCourseRepository = groupCourseRepository;
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public List<Course> getCoursesByGroupId(Long groupId) {

        System.out.println("Поиск курсов по groupId: " + groupId);

        List<GroupCourse> groupCourses = groupCourseRepository.findByGroupId(groupId);

        System.out.println("Найдено привязок группа-курс: " + groupCourses.size());

        for (GroupCourse gc : groupCourses) {
            System.out.println("GroupCourse: " + gc.getCourse().getId());
        }

        return groupCourses.stream()
                .map(groupCourse -> courseRepository.findById(groupCourse.getCourse().getId())
                        .orElseThrow(() -> new RuntimeException("Курс не найден")))
                .collect(Collectors.toList());
    }

    public List<AssignmentDTO> getAssignmentsByCourseId(Long courseId) {
        List<Assignment> assignments = assignmentRepository.findByCourseId(courseId);
        return assignments.stream()
                .map(a -> new AssignmentDTO(
                        a.getId(),
                        a.getTitle(),
                        a.getDescription(),
                        a.getDueDate()
                ))
                .toList();
    }

}


