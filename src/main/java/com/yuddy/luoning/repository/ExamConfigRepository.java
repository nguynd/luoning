package com.yuddy.luoning.repository;

import com.yuddy.luoning.entity.ExamConfig;
import com.yuddy.luoning.entity.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamConfigRepository extends JpaRepository<ExamConfig, Long> {

    Optional<ExamConfig> findByCourseIdAndExamType(Long courseId, ExamType examType);
}
