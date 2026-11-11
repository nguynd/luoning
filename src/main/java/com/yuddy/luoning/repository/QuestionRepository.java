package com.yuddy.luoning.repository;

import com.yuddy.luoning.entity.ExamType;
import com.yuddy.luoning.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Import lai: tim cau cung ma trong khoa de cap nhat
    Optional<Question> findByCourseIdAndCode(Long courseId, String code);

    List<Question> findByCourseIdOrderByCodeAsc(Long courseId);

    // Kem courseId de khong xoa nham cau hoi cua khoa khac qua URL
    Optional<Question> findByIdAndCourseId(Long id, Long courseId);

    // ... AND exam_type IN (?, ?): dem cau theo muc do cho mot loai de
    long countByCourseIdAndDifficultyAndExamTypeIn(Long courseId, int difficulty, List<ExamType> examTypes);
}
