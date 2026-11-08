package com.yuddy.luoning.repository;

import com.yuddy.luoning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByInstructorIdOrderByIdDesc(Long instructorId);
}