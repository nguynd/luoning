package com.yuddy.luoning.dto;

import com.yuddy.luoning.entity.Enrollment;

import java.time.Instant;

public class EnrollmentResponse {

    private final Long id;
    private final Long courseId;
    private final String courseTitle;
    private final double pricePaid;
    private final Instant enrolledAt;

    public EnrollmentResponse(Enrollment enrollment) {
        this.id = enrollment.getId();
        this.courseId = enrollment.getCourse().getId();
        this.courseTitle = enrollment.getCourse().getTitle();
        this.pricePaid = enrollment.getPricePaid();
        this.enrolledAt = enrollment.getEnrolledAt();
    }

    public Long getId() {
        return id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public double getPricePaid() {
        return pricePaid;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }
}
