package com.yuddy.luoning.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

// Danh sach id bai giang theo thu tu moi, phai gom du tat ca bai cua khoa hoc
public class LessonOrderRequest {

    @NotEmpty
    private List<Long> lessonIds;

    public LessonOrderRequest() {
    }

    public List<Long> getLessonIds() {
        return lessonIds;
    }

    public void setLessonIds(List<Long> lessonIds) {
        this.lessonIds = lessonIds;
    }
}
