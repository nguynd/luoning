package com.yuddy.luoning.dto;

import com.yuddy.luoning.entity.Role;
import com.yuddy.luoning.entity.User;

import java.time.Instant;

// Khong tra entity User ra ngoai de tranh lo password
public class UserResponse {

    private final Long id;
    private final String email;
    private final String fullName;
    private final Role role;
    private final Instant createdAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public Role getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
