package com.yuddy.luoning.service;

import com.yuddy.luoning.dto.LessonRequest;
import com.yuddy.luoning.dto.LessonResponse;
import com.yuddy.luoning.entity.Course;
import com.yuddy.luoning.entity.Lesson;
import com.yuddy.luoning.repository.LessonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseService courseService;

    public LessonService(LessonRepository lessonRepository, CourseService courseService) {
        this.lessonRepository = lessonRepository;
        this.courseService = courseService;
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getLessons(Long courseId) {
        courseService.findCourse(courseId); // khóa học không tồn tại → 404

        List<LessonResponse> result = new ArrayList<>();
        for (Lesson lesson : lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId)) {
            result.add(new LessonResponse(lesson));
        }
        return result;
    }

    @Transactional
    public LessonResponse createLesson(Long courseId, LessonRequest request, Long currentUserId) {
        Course course = courseService.findOwnedCourse(courseId, currentUserId);

        int nextOrder = lessonRepository.findTopByCourseIdOrderByOrderIndexDesc(courseId)
                .map(last -> last.getOrderIndex() + 1)
                .orElse(1);

        Lesson lesson = new Lesson(
                request.getTitle(),
                nextOrder,
                request.getVideoUrl(),
                request.getDurationSeconds(),
                course);

        return new LessonResponse(lessonRepository.save(lesson));
    }
}
