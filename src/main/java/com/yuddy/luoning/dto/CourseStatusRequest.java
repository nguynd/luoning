package com.yuddy.luoning.dto;

import com.yuddy.luoning.entity.CourseStatus;
import jakarta.validation.constraints.NotNull;

public class CourseStatusRequest {

    @NotNull(message = "Trang thai khong duoc de trong")
    private CourseStatus status;

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }
}
