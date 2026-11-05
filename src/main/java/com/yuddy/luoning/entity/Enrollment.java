package com.yuddy.luoning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

// Moi hoc vien chi dang ky mot khoa hoc mot lan
@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "course_id" }))
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    // Giu gia luc dang ky vi gia khoa hoc co the thay doi sau nay
    @Column(nullable = false)
    private double pricePaid;

    @Column(nullable = false, updatable = false)
    private Instant enrolledAt;

    public Enrollment() {
    }

    public Enrollment(User user, Course course, double pricePaid) {
        this.user = user;
        this.course = course;
        this.pricePaid = pricePaid;
    }

    @PrePersist
    void onCreate() {
        enrolledAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Course getCourse() {
        return course;
    }

    public double getPricePaid() {
        return pricePaid;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }
}
