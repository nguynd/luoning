package com.yuddy.luoning.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.yuddy.luoning.dto.LessonDetailResponse;
import com.yuddy.luoning.dto.LessonRequest;
import com.yuddy.luoning.dto.LessonResponse;
import com.yuddy.luoning.entity.Course;
import com.yuddy.luoning.entity.Lesson;
import com.yuddy.luoning.exception.ApiException;
import com.yuddy.luoning.repository.LessonRepository;
import com.yuddy.luoning.security.VideoTokenService;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final VideoStorageService videoStorageService;
    private final VideoTokenService videoTokenService;
    private final String publicBaseUrl;

    public LessonService(LessonRepository lessonRepository,
            CourseService courseService,
            EnrollmentService enrollmentService,
            VideoStorageService videoStorageService,
            VideoTokenService videoTokenService,
            @Value("${app.public-base-url}") String publicBaseUrl) {
        this.lessonRepository = lessonRepository;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.videoStorageService = videoStorageService;
        this.videoTokenService = videoTokenService;
        this.publicBaseUrl = publicBaseUrl;
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getLessons(Long courseId, Long viewerId) {
        courseService.findVisibleCourse(courseId, viewerId); // không có hoặc không được xem → 404

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
        Lesson lesson = findLesson(courseId, lessonId);

        boolean isInstructor = lesson.getCourse().getInstructor().getId().equals(currentUserId);
        if (!isInstructor && !enrollmentService.isEnrolled(currentUserId, courseId)) {
            throw ApiException.forbidden("Ban chua dang ky khoa hoc nay");
        }

        return toDetail(lesson, currentUserId);
    }

    @Transactional
    public LessonDetailResponse uploadVideo(Long courseId, Long lessonId, MultipartFile file, Long currentUserId) {
        courseService.findOwnedCourse(courseId, currentUserId); // không phải chủ → 403
        Lesson lesson = findLesson(courseId, lessonId);

        String oldFileName = lesson.getVideoFileName();
        lesson.setVideoFileName(videoStorageService.store(file));

        if (oldFileName != null) {
            videoStorageService.deleteAfterCommit(oldFileName); // upload lại thì xóa file cũ
        }
        return toDetail(lesson, currentUserId);
    }

    @Transactional
    public LessonDetailResponse updateLesson(Long courseId, Long lessonId, LessonRequest request, Long currentUserId) {
        courseService.findOwnedCourse(courseId, currentUserId);
        Lesson lesson = findLesson(courseId, lessonId);

        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setDurationSeconds(request.getDurationSeconds());

        return toDetail(lesson, currentUserId);
    }

    @Transactional
    public void deleteLesson(Long courseId, Long lessonId, Long currentUserId) {
        courseService.findOwnedCourse(courseId, currentUserId);
        Lesson lesson = findLesson(courseId, lessonId);

        if (lesson.getVideoFileName() != null) {
            videoStorageService.deleteAfterCommit(lesson.getVideoFileName());
        }
        lessonRepository.delete(lesson);

        // Danh so lai cac bai con lai de khong bi hong thu tu (1, 2, 4 → 1, 2, 3)
        List<Lesson> remaining = lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setOrderIndex(i + 1);
        }
    }

    @Transactional
    public List<LessonResponse> reorderLessons(Long courseId, List<Long> lessonIds, Long currentUserId) {
        courseService.findOwnedCourse(courseId, currentUserId);
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId);

        // Danh sach gui len phai co dung tat ca bai cua khoa hoc, moi bai mot lan
        Map<Long, Lesson> byId = new HashMap<>();
        for (Lesson lesson : lessons) {
            byId.put(lesson.getId(), lesson);
        }
        if (lessonIds.size() != lessons.size() || !byId.keySet().equals(new HashSet<>(lessonIds))) {
            throw ApiException.badRequest("Danh sach bai giang khong khop voi khoa hoc");
        }

        List<LessonResponse> result = new ArrayList<>();
        for (int i = 0; i < lessonIds.size(); i++) {
            Lesson lesson = byId.get(lessonIds.get(i));
            lesson.setOrderIndex(i + 1);
            result.add(new LessonResponse(lesson));
        }
        return result;
    }

    // Quyen da duoc kiem tra luc cap token o getLesson, o day chi kiem tra token
    @Transactional(readOnly = true)
    public Resource loadVideo(Long courseId, Long lessonId, String token) {
        videoTokenService.verify(token, lessonId);

        Lesson lesson = findLesson(courseId, lessonId);
        if (lesson.getVideoFileName() == null) {
            throw ApiException.notFound("Bai giang chua co video");
        }
        return videoStorageService.load(lesson.getVideoFileName());
    }

    // Bai giang phai thuoc dung khoa hoc tren URL
    private Lesson findLesson(Long courseId, Long lessonId) {
        return lessonRepository.findByIdAndCourseId(lessonId, courseId)
                .orElseThrow(() -> ApiException.notFound("Khong tim thay bai giang"));
    }

    // Video da upload thi tra link phat kem token, chua co thi giu link ngoai cu
    private LessonDetailResponse toDetail(Lesson lesson, Long userId) {
        String videoUrl = lesson.getVideoUrl();
        if (lesson.getVideoFileName() != null) {
            String token = videoTokenService.createToken(userId, lesson.getId());
            videoUrl = publicBaseUrl + "/api/courses/" + lesson.getCourse().getId()
                    + "/lessons/" + lesson.getId() + "/video?token=" + token;
        }
        return new LessonDetailResponse(lesson, videoUrl);
    }
}
