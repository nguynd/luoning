package com.yuddy.luoning.dto;

import com.yuddy.luoning.entity.ExamConfig;
import com.yuddy.luoning.entity.ExamType;

// available*: so cau ngan hang dang co theo tung muc, de giang vien biet nen dat bao nhieu
public record ExamConfigResponse(
        ExamType examType,
        int level1Count,
        int level2Count,
        int level3Count,
        int totalQuestions,
        int durationMinutes,
        boolean open,
        long availableLevel1,
        long availableLevel2,
        long availableLevel3) {

    public ExamConfigResponse(ExamConfig c, long[] available) {
        this(c.getExamType(), c.getLevel1Count(), c.getLevel2Count(), c.getLevel3Count(),
                c.getTotalQuestions(), c.getDurationMinutes(), c.isOpen(),
                available[0], available[1], available[2]);
    }
}
