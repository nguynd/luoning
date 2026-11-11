package com.yuddy.luoning.service;

import com.yuddy.luoning.dto.QuestionResponse;
import com.yuddy.luoning.entity.Question;
import com.yuddy.luoning.exception.ApiException;
import com.yuddy.luoning.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Xem va xoa cau hoi trong ngan hang; import nam o QuestionImportService
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CourseService courseService;

    public QuestionService(QuestionRepository questionRepository, CourseService courseService) {
        this.questionRepository = questionRepository;
        this.courseService = courseService;
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestions(Long courseId, Long userId) {
        courseService.findOwnedCourse(courseId, userId); // 404 / 403
        return questionRepository.findByCourseIdOrderByCodeAsc(courseId).stream()
                .map(QuestionResponse::new)
                .toList();
    }

    // Bai thi luu ban chup cau hoi, nen xoa cau hoi khong lam hong bai thi cu
    @Transactional
    public void deleteQuestion(Long courseId, Long questionId, Long userId) {
        courseService.findOwnedCourse(courseId, userId);
        Question question = questionRepository.findByIdAndCourseId(questionId, courseId)
                .orElseThrow(() -> ApiException.notFound("Khong tim thay cau hoi"));
        questionRepository.delete(question);
    }
}
