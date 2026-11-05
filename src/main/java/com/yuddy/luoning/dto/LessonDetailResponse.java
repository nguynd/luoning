package com.yuddy.luoning.dto;

import com.yuddy.luoning.entity.Lesson;

// Co them videoUrl, chi tra cho hoc vien da dang ky hoac giang vien cua khoa hoc
public class LessonDetailResponse extends LessonResponse {

    private final String videoUrl;

    public LessonDetailResponse(Lesson lesson) {
        super(lesson);
        this.videoUrl = lesson.getVideoUrl();
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
