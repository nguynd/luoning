package com.yuddy.luoning.repository;

import com.yuddy.luoning.entity.Course;
import com.yuddy.luoning.entity.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByInstructorIdOrderByIdDesc(Long instructorId);

    // title LIKE %keyword% (khong phan biet hoa thuong); keyword rong thi khop tat ca
    Page<Course> findByStatusAndTitleContainingIgnoreCase(CourseStatus status, String keyword, Pageable pageable);
}