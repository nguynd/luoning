package com.yuddy.luoning.controller;

import com.yuddy.luoning.dto.LessonDetailResponse;
import com.yuddy.luoning.dto.LessonOrderRequest;
import com.yuddy.luoning.dto.LessonRequest;
import com.yuddy.luoning.dto.LessonResponse;
import com.yuddy.luoning.security.CurrentUser;
import com.yuddy.luoning.service.LessonService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping
    public List<LessonResponse> getLessons(@PathVariable Long courseId, @AuthenticationPrincipal Jwt jwt) {
        return lessonService.getLessons(courseId, CurrentUser.idOrNull(jwt));
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

    @PutMapping("/{lessonId}")
    public LessonDetailResponse updateLesson(@PathVariable Long courseId,
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return lessonService.updateLesson(courseId, lessonId, request, CurrentUser.id(jwt));
    }

    @DeleteMapping("/{lessonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(@PathVariable Long courseId,
            @PathVariable Long lessonId,
            @AuthenticationPrincipal Jwt jwt) {
        lessonService.deleteLesson(courseId, lessonId, CurrentUser.id(jwt));
    }

    // Duong dan "/order" co dinh nen Spring uu tien hon "/{lessonId}"
    @PutMapping("/order")
    public List<LessonResponse> reorderLessons(@PathVariable Long courseId,
            @Valid @RequestBody LessonOrderRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return lessonService.reorderLessons(courseId, request.getLessonIds(), CurrentUser.id(jwt));
    }

    @PostMapping(path = "/{lessonId}/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LessonDetailResponse uploadVideo(@PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {
        return lessonService.uploadVideo(courseId, lessonId, file, CurrentUser.id(jwt));
    }

    // Tra ve Resource nen Spring tu xu ly header Range (tua video, tra 206)
    @GetMapping("/{lessonId}/video")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestParam String token) {
        Resource video = lessonService.loadVideo(courseId, lessonId, token);
        MediaType type = MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(type).body(video);
    }
}
