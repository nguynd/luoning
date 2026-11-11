package com.yuddy.luoning.repository;

import com.yuddy.luoning.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Import lai: tim cau cung ma trong khoa de cap nhat
    Optional<Question> findByCourseIdAndCode(Long courseId, String code);

    List<Question> findByCourseIdOrderByCodeAsc(Long courseId);
}
