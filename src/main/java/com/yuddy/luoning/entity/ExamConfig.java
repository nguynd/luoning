package com.yuddy.luoning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// Cau hinh de thi: moi khoa co toi da 1 de giua ky (MIDTERM) va 1 de cuoi ky (FINAL)
@Entity
@Table(name = "exam_configs", uniqueConstraints = @UniqueConstraint(columnNames = { "course_id", "exam_type" }))
public class ExamConfig {

    public static final int DEFAULT_DURATION_MINUTES = 45;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamType examType; // chi MIDTERM hoac FINAL

    // So cau lay ngau nhien theo tung muc do kho
    private int level1Count;
    private int level2Count;
    private int level3Count;

    private int durationMinutes = DEFAULT_DURATION_MINUTES;

    // false: hoc vien chua duoc bat dau thi
    private boolean open;

    public ExamConfig() {
    }

    public ExamConfig(Course course, ExamType examType) {
        this.course = course;
        this.examType = examType;
    }

    public int getTotalQuestions() {
        return level1Count + level2Count + level3Count;
    }

    // muc do 1..3 -> so cau, de vong lap xu ly ca 3 muc giong nhau
    public int getCount(int level) {
        return switch (level) {
            case 1 -> level1Count;
            case 2 -> level2Count;
            case 3 -> level3Count;
            default -> throw new IllegalArgumentException("Muc do khong hop le: " + level);
        };
    }

    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public ExamType getExamType() {
        return examType;
    }

    public int getLevel1Count() {
        return level1Count;
    }

    public void setLevel1Count(int level1Count) {
        this.level1Count = level1Count;
    }

    public int getLevel2Count() {
        return level2Count;
    }

    public void setLevel2Count(int level2Count) {
        this.level2Count = level2Count;
    }

    public int getLevel3Count() {
        return level3Count;
    }

    public void setLevel3Count(int level3Count) {
        this.level3Count = level3Count;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
