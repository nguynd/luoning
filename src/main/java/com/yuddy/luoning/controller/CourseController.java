package com.yuddy.luoning.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yuddy.luoning.dto.CourseRequest;
import com.yuddy.luoning.dto.CourseResponse;
import com.yuddy.luoning.security.CurrentUser;
import com.yuddy.luoning.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping
    public CourseResponse createCourse(@Valid @RequestBody CourseRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return courseService.createCourse(request, CurrentUser.id(jwt));
    }
}