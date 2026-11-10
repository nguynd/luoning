package com.yuddy.luoning.service;

import com.yuddy.luoning.dto.EnrollmentResponse;
import com.yuddy.luoning.entity.Course;
import com.yuddy.luoning.entity.CourseStatus;
import com.yuddy.luoning.entity.Enrollment;
import com.yuddy.luoning.entity.User;
import com.yuddy.luoning.exception.ApiException;
import com.yuddy.luoning.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;
    private final UserService userService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
            CourseService courseService,
            UserService userService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseService = courseService;
        this.userService = userService;
    }

    @Transactional
    public EnrollmentResponse enroll(Long courseId, Long userId) {
        Course course = courseService.findCourse(courseId); // không có → 404

        // Chi dang ky moi duoc khoa dang ban
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw ApiException.notFound("Khong tim thay khoa hoc");
        }

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw ApiException.conflict("Ban da dang ky khoa hoc nay");
        }

        User user = userService.getById(userId);
        Enrollment enrollment = new Enrollment(user, course, course.getPrice());

        return new EnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(Long userId) {
        List<EnrollmentResponse> result = new ArrayList<>();
        for (Enrollment enrollment : enrollmentRepository.findByUserId(userId)) {
            result.add(new EnrollmentResponse(enrollment));
        }
        return result;
    }

    public boolean isEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

}
