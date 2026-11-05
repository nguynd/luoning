package com.yuddy.luoning.controller;

import com.yuddy.luoning.dto.EnrollmentResponse;
import com.yuddy.luoning.security.CurrentUser;
import com.yuddy.luoning.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/api/courses/{courseId}/enroll")
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse enroll(@PathVariable Long courseId, @AuthenticationPrincipal Jwt jwt) {
        return enrollmentService.enroll(courseId, CurrentUser.id(jwt));
    }

    @GetMapping("/api/enrollments/me")
    public List<EnrollmentResponse> getMyEnrollments(@AuthenticationPrincipal Jwt jwt) {
        return enrollmentService.getMyEnrollments(CurrentUser.id(jwt));
    }
}
