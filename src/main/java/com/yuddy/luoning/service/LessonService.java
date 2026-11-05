package com.yuddy.luoning.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yuddy.luoning.dto.LessonDetailResponse;
import com.yuddy.luoning.dto.LessonRequest;
import com.yuddy.luoning.dto.LessonResponse;
import com.yuddy.luoning.entity.Course;
import com.yuddy.luoning.entity.Lesson;
import com.yuddy.luoning.exception.ApiException;
import com.yuddy.luoning.repository.LessonRepository;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public LessonService(LessonRepository lessonRepository,
            CourseService courseService,
            EnrollmentService enrollmentService) {
        this.lessonRepository = lessonRepository;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
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

    @Transactional(readOnly = true)
    public LessonDetailResponse getLesson(Long courseId, Long lessonId, Long currentUserId) {
        Lesson lesson = lessonRepository.findByIdAndCourseId(lessonId, courseId)
                .orElseThrow(() -> ApiException.notFound("Khong tim thay bai giang"));

        boolean isInstructor = lesson.getCourse().getInstructor().getId().equals(currentUserId);
        if (!isInstructor && !enrollmentService.isEnrolled(currentUserId, courseId)) {
            throw ApiException.forbidden("Ban chua dang ky khoa hoc nay");
        }

        return new LessonDetailResponse(lesson);
    }
}
