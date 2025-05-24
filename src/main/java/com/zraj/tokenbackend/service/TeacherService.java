package com.zraj.tokenbackend.service;

import com.zraj.tokenbackend.dto.teacher.GroupAssignmentDTO;
import com.zraj.tokenbackend.dto.teacher.StudentAssignmentDTO;
import com.zraj.tokenbackend.dto.teacher.SubmittedAssignmentDTO;
import com.zraj.tokenbackend.dto.teacher.TeacherAssignmentDTO;
import com.zraj.tokenbackend.entity.AssignmentSubmission;
import com.zraj.tokenbackend.entity.Course;
import com.zraj.tokenbackend.repository.AssignmentSubmissionRepository;
import com.zraj.tokenbackend.repository.CourseTeacherRepository;
import com.zraj.tokenbackend.repository.GroupCourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final CourseTeacherRepository courseTeacherRepository;
    private final GroupCourseRepository groupCourseRepository;
    private final AssignmentSubmissionRepository submissionRepository;

    public TeacherService(CourseTeacherRepository courseTeacherRepository, GroupCourseRepository groupCourseRepository, AssignmentSubmissionRepository submissionRepository) {
        this.courseTeacherRepository = courseTeacherRepository;
        this.groupCourseRepository = groupCourseRepository;
        this.submissionRepository = submissionRepository;
    }

    public List<TeacherAssignmentDTO> getTeacherAssignments(Long teacherId) {
        // 1. Получаем курсы преподавателя
        List<Course> courses = courseTeacherRepository.findCoursesByTeacherId(teacherId);

        return courses.stream()
                .map(course -> {
                    // 2. Для каждого курса получаем группы
                    List<GroupAssignmentDTO> groups = groupCourseRepository
                            .findGroupsByCourseId(course.getId())
                            .stream()
                            .map(group -> new GroupAssignmentDTO(
                                    group.getId(),
                                    group.getName(),
                                    getStudentsWithAssignments(course.getId(), group.getId())
                            )).toList();

                    return new TeacherAssignmentDTO(
                            course.getId(),
                            course.getName(),
                            groups
                    );
                })
                .toList();
    }

    private List<StudentAssignmentDTO> getStudentsWithAssignments(Long courseId, Long groupId) {
        return submissionRepository.findByCourseAndGroup(courseId, groupId)
                .stream()
                .collect(Collectors.groupingBy(
                        AssignmentSubmission::getStudent,
                        Collectors.mapping(
                                submission -> new SubmittedAssignmentDTO(
                                        submission.getId(),
                                        submission.getAssignment().getTitle(),
                                        submission.getFilePath(),
                                        submission.getFileName(),
                                        submission.getSubmittedAt()
                                ),
                                Collectors.toList()
                        )
                ))
                .entrySet()
                .stream()
                .map(entry -> new StudentAssignmentDTO(
                        entry.getKey().getUserId(),
                        entry.getKey().getUser().getLastName(),
                        entry.getValue()
                ))
                .toList();
    }
}