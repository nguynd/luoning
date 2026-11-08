package com.yuddy.luoning.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yuddy.luoning.dto.CourseRequest;
import com.yuddy.luoning.dto.CourseResponse;
import com.yuddy.luoning.entity.Course;
import com.yuddy.luoning.entity.Lesson;
import com.yuddy.luoning.entity.User;
import com.yuddy.luoning.exception.ApiException;
import com.yuddy.luoning.repository.CourseRepository;
import com.yuddy.luoning.repository.EnrollmentRepository;
import com.yuddy.luoning.repository.LessonRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserService userService;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final VideoStorageService videoStorageService;

    public CourseService(CourseRepository courseRepository,
            UserService userService,
            LessonRepository lessonRepository,
            EnrollmentRepository enrollmentRepository,
            VideoStorageService videoStorageService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.videoStorageService = videoStorageService;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        List<CourseResponse> result = new ArrayList<>();
        for (Course course : courseRepository.findAll()) {
            result.add(new CourseResponse(course));
        }
        return result;
    }

    // Khoa hoc cua giang vien dang dang nhap, moi nhat len dau
    @Transactional(readOnly = true)
    public List<CourseResponse> getMyCourses(Long instructorId) {
        List<CourseResponse> result = new ArrayList<>();
        for (Course course : courseRepository.findByInstructorIdOrderByIdDesc(instructorId)) {
            result.add(new CourseResponse(course));
        }
        return result;
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request, Long instructorId) {
        User instructor = userService.getById(instructorId);

        Course course = new Course(
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                instructor);

        return new CourseResponse(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        return new CourseResponse(findCourse(id));
    }

    public Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Khong tim thay khoa hoc"));
    }

    public Course findOwnedCourse(Long id, Long userId) {
        Course course = findCourse(id);
        checkOwner(course, userId);
        return course;
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request, Long currentUserId) {
        Course course = findCourse(id);
        checkOwner(course, currentUserId);

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());

        return new CourseResponse(course);
    }

    private void checkOwner(Course course, Long userId) {
        if (!course.getInstructor().getId().equals(userId)) {
            throw ApiException.forbidden("Ban khong phai giang vien cua khoa hoc nay");
        }
    }

    @Transactional
    public void deleteCourse(Long id, Long currentUserId) {
        Course course = findCourse(id); // không có → 404
        checkOwner(course, currentUserId); // không phải chủ → 403

        // Hoc vien da dang ky (co the da tra tien) thi khong cho xoa
        if (enrollmentRepository.existsByCourseId(id)) {
            throw ApiException.conflict("Khoa hoc da co hoc vien dang ky, khong the xoa");
        }

        // Xoa bai giang va file video truoc, neu khong khoa ngoai se chan xoa khoa hoc
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(id);
        for (Lesson lesson : lessons) {
            if (lesson.getVideoFileName() != null) {
                videoStorageService.deleteAfterCommit(lesson.getVideoFileName());
            }
        }
        lessonRepository.deleteAll(lessons);
        courseRepository.delete(course);
    }

}