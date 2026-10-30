package com.yuddy.luoning.repository;

import com.yuddy.luoning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}