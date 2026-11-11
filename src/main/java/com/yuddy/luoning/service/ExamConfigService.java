package com.yuddy.luoning.service;

import com.yuddy.luoning.dto.ExamConfigRequest;
import com.yuddy.luoning.dto.ExamConfigResponse;
import com.yuddy.luoning.entity.Course;
import com.yuddy.luoning.entity.ExamConfig;
import com.yuddy.luoning.entity.ExamType;
import com.yuddy.luoning.exception.ApiException;
import com.yuddy.luoning.repository.ExamConfigRepository;
import com.yuddy.luoning.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamConfigService {

    private static final int LEVELS = 3;

    private final ExamConfigRepository examConfigRepository;
    private final QuestionRepository questionRepository;
    private final CourseService courseService;

    public ExamConfigService(ExamConfigRepository examConfigRepository,
            QuestionRepository questionRepository,
            CourseService courseService) {
        this.examConfigRepository = examConfigRepository;
        this.questionRepository = questionRepository;
        this.courseService = courseService;
    }

    // Chua cau hinh thi tra ve mac dinh (0 cau, dong) de giang vien co du lieu dien form
    @Transactional(readOnly = true)
    public ExamConfigResponse getConfig(Long courseId, ExamType examType, Long userId) {
        Course course = courseService.findOwnedCourse(courseId, userId);
        checkExamType(examType);
        ExamConfig config = examConfigRepository.findByCourseIdAndExamType(courseId, examType)
                .orElseGet(() -> new ExamConfig(course, examType));
        return new ExamConfigResponse(config, countAvailable(courseId, examType));
    }

    @Transactional
    public ExamConfigResponse saveConfig(Long courseId, ExamType examType, ExamConfigRequest request, Long userId) {
        Course course = courseService.findOwnedCourse(courseId, userId);
        checkExamType(examType);

        int[] wanted = { request.level1Count(), request.level2Count(), request.level3Count() };
        if (wanted[0] + wanted[1] + wanted[2] == 0) {
            throw ApiException.badRequest("De thi phai co it nhat 1 cau");
        }

        // Gom loi ca 3 muc mot lan, giong cach import
        long[] available = countAvailable(courseId, examType);
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < LEVELS; i++) {
            if (wanted[i] > available[i]) {
                errors.add("Muc do " + (i + 1) + ": can " + wanted[i]
                        + " cau nhung ngan hang chi co " + available[i] + " cau");
            }
        }
        if (!errors.isEmpty()) {
            throw ApiException.badRequest("Ngan hang khong du cau hoi", errors);
        }

        // Upsert: co roi thi sua, chua co thi tao
        ExamConfig config = examConfigRepository.findByCourseIdAndExamType(courseId, examType)
                .orElseGet(() -> new ExamConfig(course, examType));
        config.setLevel1Count(wanted[0]);
        config.setLevel2Count(wanted[1]);
        config.setLevel3Count(wanted[2]);
        config.setDurationMinutes(request.durationMinutes());
        config.setOpen(request.open());
        examConfigRepository.save(config);

        return new ExamConfigResponse(config, available);
    }

    // BOTH la loai cua cau hoi, khong phai cua de thi
    private void checkExamType(ExamType examType) {
        if (examType == ExamType.BOTH) {
            throw ApiException.badRequest("Loai de chi duoc la MIDTERM hoac FINAL");
        }
    }

    // De giua ky dung cau MIDTERM + BOTH; de cuoi ky dung cau FINAL + BOTH
    long[] countAvailable(Long courseId, ExamType examType) {
        List<ExamType> types = List.of(examType, ExamType.BOTH);
        long[] result = new long[LEVELS];
        for (int i = 0; i < LEVELS; i++) {
            result[i] = questionRepository.countByCourseIdAndDifficultyAndExamTypeIn(courseId, i + 1, types);
        }
        return result;
    }
}
