package com.yuddy.luoning.controller;

import com.yuddy.luoning.dto.RegisterRequest;
import com.yuddy.luoning.dto.UserResponse;
import com.yuddy.luoning.entity.Role;
import com.yuddy.luoning.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// API chi danh cho admin, duoc chan theo role trong SecurityConfig
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/instructors")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createInstructor(@Valid @RequestBody RegisterRequest request) {
        return userService.createInstructor(request);
    }

    // GET /api/admin/users hoac /api/admin/users?role=INSTRUCTOR
    @GetMapping("/users")
    public List<UserResponse> getUsers(@RequestParam(required = false) Role role) {
        return userService.getUsers(role);
    }
}
