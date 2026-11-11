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

import java.util.ArrayList;
import java.util.List;

// Mot cau hoi trong ngan hang cua mot khoa hoc, ma cau hoi la duy nhat trong khoa
@Entity
@Table(name = "questions", uniqueConstraints = @UniqueConstraint(columnNames = { "course_id", "code" }))
public class Question {

    public static final int MAX_OPTIONS = 6; // A..F

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 2000)
    private String content;

    // A, B bat buoc; C..F co the trong
    @Column(nullable = false, length = 1000)
    private String optionA;
    @Column(nullable = false, length = 1000)
    private String optionB;
    @Column(length = 1000)
    private String optionC;
    @Column(length = 1000)
    private String optionD;
    @Column(length = 1000)
    private String optionE;
    @Column(length = 1000)
    private String optionF;

    // true: duoc chon nhieu dap an
    @Column(nullable = false)
    private boolean multipleChoice;

    // Cac chu cai dung, viet hoa va sap xep, vi du "AC"
    @Column(nullable = false, length = MAX_OPTIONS)
    private String correctAnswers;

    @Column(nullable = false)
    private int difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamType examType;

    public Question() {
    }

    public Question(Course course, String code) {
        this.course = course;
        this.code = code;
    }

    // Tra ve cac dap an khong rong theo thu tu A, B, C...
    public List<String> getOptions() {
        List<String> options = new ArrayList<>();
        for (String option : List.of(nz(optionA), nz(optionB), nz(optionC), nz(optionD), nz(optionE), nz(optionF))) {
            if (!option.isEmpty()) {
                options.add(option);
            }
        }
        return options;
    }

    // options[0] = A, options[1] = B, ...; phan tu thieu thi de null
    public void setOptions(List<String> options) {
        optionA = get(options, 0);
        optionB = get(options, 1);
        optionC = get(options, 2);
        optionD = get(options, 3);
        optionE = get(options, 4);
        optionF = get(options, 5);
    }

    private static String get(List<String> list, int index) {
        return index < list.size() ? list.get(index) : null;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public String getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public String getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(String correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public ExamType getExamType() {
        return examType;
    }

    public void setExamType(ExamType examType) {
        this.examType = examType;
    }
}
