package com.yuddy.luoning.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yuddy.luoning.dto.CourseRequest;
import com.yuddy.luoning.dto.CourseResponse;
import com.yuddy.luoning.dto.CourseStatusRequest;
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

    @GetMapping("/{id}")
    public CourseResponse getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @PutMapping("/{id}")
    public CourseResponse updateCourse(@PathVariable Long id,
            @Valid @RequestBody CourseRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return courseService.updateCourse(id, request, CurrentUser.id(jwt));
    }

    // Body: { "status": "PUBLISHED" } (DRAFT | PUBLISHED | ARCHIVED)
    @PutMapping("/{id}/status")
    public CourseResponse changeStatus(@PathVariable Long id,
            @Valid @RequestBody CourseStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return courseService.changeStatus(id, request.getStatus(), CurrentUser.id(jwt));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        courseService.deleteCourse(id, CurrentUser.id(jwt));
    }

}