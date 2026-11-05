package com.yuddy.luoning.controller;

import com.yuddy.luoning.dto.LessonDetailResponse;
import com.yuddy.luoning.dto.LessonRequest;
import com.yuddy.luoning.dto.LessonResponse;
import com.yuddy.luoning.security.CurrentUser;
import com.yuddy.luoning.service.LessonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping
    public List<LessonResponse> getLessons(@PathVariable Long courseId) {
        return lessonService.getLessons(courseId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonResponse createLesson(@PathVariable Long courseId,
            @Valid @RequestBody LessonRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return lessonService.createLesson(courseId, request, CurrentUser.id(jwt));
    }

    @GetMapping("/{lessonId}")
    public LessonDetailResponse getLesson(@PathVariable Long courseId,
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Jwt jwt) {
        return lessonService.getLesson(courseId, lessonId, CurrentUser.id(jwt));
    }
}
