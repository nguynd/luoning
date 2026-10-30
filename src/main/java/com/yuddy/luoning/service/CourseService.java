package com.yuddy.luoning.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yuddy.luoning.dto.CourseRequest;
import com.yuddy.luoning.dto.CourseResponse;
import com.yuddy.luoning.entity.Course;
import com.yuddy.luoning.entity.User;
import com.yuddy.luoning.exception.ApiException;
import com.yuddy.luoning.repository.CourseRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserService userService;

    public CourseService(CourseRepository courseRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        List<CourseResponse> result = new ArrayList<>();
        for (Course course : courseRepository.findAll()) {
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

        courseRepository.delete(course);
    }

}