package com.yuddy.luoning.dto;

import com.yuddy.luoning.entity.Lesson;

// Khong co videoUrl: danh sach bai giang la cong khai, video chi danh cho hoc vien da dang ky
public class LessonResponse {

    private final Long id;
    private final Long courseId;
    private final String title;
    private final int orderIndex;
    private final Integer durationSeconds;

    public LessonResponse(Lesson lesson) {
        this.id = lesson.getId();
        this.courseId = lesson.getCourse().getId();
        this.title = lesson.getTitle();
        this.orderIndex = lesson.getOrderIndex();
        this.durationSeconds = lesson.getDurationSeconds();
    }

    public Long getId() {
        return id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }
}
