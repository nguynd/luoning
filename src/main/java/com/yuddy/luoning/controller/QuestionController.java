package com.yuddy.luoning.controller;

import com.yuddy.luoning.dto.QuestionImportResponse;
import com.yuddy.luoning.dto.QuestionResponse;
import com.yuddy.luoning.security.CurrentUser;
import com.yuddy.luoning.service.QuestionImportService;
import com.yuddy.luoning.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// Ngan hang cau hoi cua mot khoa hoc, chi giang vien cua khoa
@RestController
@RequestMapping("/api/courses/{courseId}/questions")
public class QuestionController {

    private final QuestionImportService questionImportService;
    private final QuestionService questionService;

    public QuestionController(QuestionImportService questionImportService, QuestionService questionService) {
        this.questionImportService = questionImportService;
        this.questionService = questionService;
    }

    @GetMapping
    public List<QuestionResponse> getQuestions(@PathVariable Long courseId, @AuthenticationPrincipal Jwt jwt) {
        return questionService.getQuestions(courseId, CurrentUser.id(jwt));
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable Long courseId, @PathVariable Long questionId,
            @AuthenticationPrincipal Jwt jwt) {
        questionService.deleteQuestion(courseId, questionId, CurrentUser.id(jwt));
    }

    // form-data: file = <file .xlsx>; loai de nam o cot L cua tung cau
    @PostMapping("/import")
    public QuestionImportResponse importQuestions(@PathVariable Long courseId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {
        return questionImportService.importQuestions(courseId, file, CurrentUser.id(jwt));
    }
}
