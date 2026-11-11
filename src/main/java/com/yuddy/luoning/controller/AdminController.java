package com.yuddy.luoning.controller;

import com.yuddy.luoning.dto.PageResponse;
import com.yuddy.luoning.dto.RegisterRequest;
import com.yuddy.luoning.dto.UserResponse;
import com.yuddy.luoning.entity.Role;
import com.yuddy.luoning.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public PageResponse<UserResponse> getUsers(@RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return userService.getUsers(role, page, size);
    }

    @PutMapping("/users/{id}/lock")
    public UserResponse lockUser(@PathVariable Long id) {
        return userService.setLocked(id, true);
    }

    @PutMapping("/users/{id}/unlock")
    public UserResponse unlockUser(@PathVariable Long id) {
        return userService.setLocked(id, false);
    }
}
