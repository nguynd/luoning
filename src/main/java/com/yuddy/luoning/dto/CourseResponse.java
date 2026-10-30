package com.yuddy.luoning.dto;

import com.yuddy.luoning.entity.Course;

public class CourseResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final double price;
    private final Long instructorId;
    private final String instructorName;

    public CourseResponse(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.price = course.getPrice();
        this.instructorId = course.getInstructor().getId();
        this.instructorName = course.getInstructor().getFullName();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

}
