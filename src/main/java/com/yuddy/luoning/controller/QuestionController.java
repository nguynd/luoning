package com.yuddy.luoning.controller;

import com.yuddy.luoning.dto.QuestionImportResponse;
import com.yuddy.luoning.security.CurrentUser;
import com.yuddy.luoning.service.QuestionImportService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// Ngan hang cau hoi cua mot khoa hoc, chi giang vien cua khoa
@RestController
@RequestMapping("/api/courses/{courseId}/questions")
public class QuestionController {

    private final QuestionImportService questionImportService;

    public QuestionController(QuestionImportService questionImportService) {
        this.questionImportService = questionImportService;
    }

    // form-data: file = <file .xlsx>; loai de nam o cot L cua tung cau
    @PostMapping("/import")
    public QuestionImportResponse importQuestions(@PathVariable Long courseId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {
        return questionImportService.importQuestions(courseId, file, CurrentUser.id(jwt));
    }
}
