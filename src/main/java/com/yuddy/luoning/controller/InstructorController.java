package com.yuddy.luoning.controller;

import com.yuddy.luoning.dto.CourseResponse;
import com.yuddy.luoning.security.CurrentUser;
import com.yuddy.luoning.service.CourseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// API chi danh cho giang vien, duoc chan theo role trong SecurityConfig
@RestController
@RequestMapping("/api/instructor")
public class InstructorController {

    private final CourseService courseService;

    public InstructorController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public List<CourseResponse> getMyCourses(@AuthenticationPrincipal Jwt jwt) {
        return courseService.getMyCourses(CurrentUser.id(jwt));
    }
}
