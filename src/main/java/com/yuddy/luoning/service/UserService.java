package com.yuddy.luoning.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yuddy.luoning.dto.PageResponse;
import com.yuddy.luoning.dto.RegisterRequest;
import com.yuddy.luoning.dto.UserResponse;
import com.yuddy.luoning.entity.Role;
import com.yuddy.luoning.entity.User;
import com.yuddy.luoning.exception.ApiException;
import com.yuddy.luoning.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Dung chung cho hoc vien tu dang ky va admin tao tai khoan giang vien
    @Transactional
    public User createUser(RegisterRequest request, Role role) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw ApiException.conflict("Email da duoc su dung");
        }

        User user = new User(
                email,
                passwordEncoder.encode(request.getPassword()),
                request.getFullName().trim(),
                role);
        return userRepository.save(user);
    }

    @Transactional
    public UserResponse createInstructor(RegisterRequest request) {
        return new UserResponse(createUser(request, Role.INSTRUCTOR));
    }

    // role == null nghia la lay tat ca
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsers(Role role, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, 50);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> users = (role == null)
                ? userRepository.findAllBy(pageable)
                : userRepository.findByRole(role, pageable);
        return new PageResponse<>(users.map(UserResponse::new));
    }

    // Khoa hoac mo khoa tai khoan, khong cho khoa admin
    @Transactional
    public UserResponse setLocked(Long id, boolean locked) {
        User user = getById(id);
        if (user.getRole() == Role.ADMIN) {
            throw ApiException.badRequest("Khong the khoa tai khoan admin");
        }
        user.setLocked(locked); // dirty checking tu UPDATE khi commit
        return new UserResponse(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Khong tim thay nguoi dung"));
    }

    public UserResponse getMe(Long id) {
        return new UserResponse(getById(id));
    }
}
