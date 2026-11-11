package com.yuddy.luoning.controller;

import com.yuddy.luoning.dto.ExamConfigRequest;
import com.yuddy.luoning.dto.ExamConfigResponse;
import com.yuddy.luoning.entity.ExamType;
import com.yuddy.luoning.security.CurrentUser;
import com.yuddy.luoning.service.ExamConfigService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Giang vien cau hinh de: /api/courses/6/exams/MIDTERM/config
@RestController
@RequestMapping("/api/courses/{courseId}/exams/{examType}/config")
public class ExamConfigController {

    private final ExamConfigService examConfigService;

    public ExamConfigController(ExamConfigService examConfigService) {
        this.examConfigService = examConfigService;
    }

    @GetMapping
    public ExamConfigResponse getConfig(@PathVariable Long courseId, @PathVariable ExamType examType,
            @AuthenticationPrincipal Jwt jwt) {
        return examConfigService.getConfig(courseId, examType, CurrentUser.id(jwt));
    }

    @PutMapping
    public ExamConfigResponse saveConfig(@PathVariable Long courseId, @PathVariable ExamType examType,
            @Valid @RequestBody ExamConfigRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return examConfigService.saveConfig(courseId, examType, request, CurrentUser.id(jwt));
    }
}
