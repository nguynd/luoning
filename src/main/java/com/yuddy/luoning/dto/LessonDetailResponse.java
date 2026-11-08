package com.yuddy.luoning.dto;

import com.yuddy.luoning.entity.Lesson;

// Co them videoUrl, chi tra cho hoc vien da dang ky hoac giang vien cua khoa hoc
public class LessonDetailResponse extends LessonResponse {

    private final String videoUrl;

    // videoUrl do service tinh: link phat video co token, hoac link ngoai cu
    public LessonDetailResponse(Lesson lesson, String videoUrl) {
        super(lesson);
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
