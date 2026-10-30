package com.yuddy.luoning.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yuddy.luoning.dto.CourseRequest;
import com.yuddy.luoning.dto.CourseResponse;
import com.yuddy.luoning.entity.Course;
import com.yuddy.luoning.entity.User;
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
}