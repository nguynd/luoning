package com.yuddy.luoning.dto;

import com.yuddy.luoning.entity.ExamType;
import com.yuddy.luoning.entity.Question;

import java.util.List;

// CHI danh cho giang vien: co dap an dung. De thi gui cho hoc vien se dung DTO khac.
public record QuestionResponse(
        Long id,
        String code,
        String content,
        List<String> options,
        boolean multipleChoice,
        String correctAnswers,
        int difficulty,
        ExamType examType) {

    public QuestionResponse(Question q) {
        this(q.getId(), q.getCode(), q.getContent(), q.getOptions(), q.isMultipleChoice(),
                q.getCorrectAnswers(), q.getDifficulty(), q.getExamType());
    }
}
