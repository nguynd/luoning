package com.yuddy.luoning.service;

import com.yuddy.luoning.dto.AuthResponse;
import com.yuddy.luoning.dto.LoginRequest;
import com.yuddy.luoning.dto.RegisterRequest;
import com.yuddy.luoning.dto.UserResponse;
import com.yuddy.luoning.entity.Role;
import com.yuddy.luoning.entity.User;
import com.yuddy.luoning.exception.ApiException;
import com.yuddy.luoning.repository.UserRepository;
import com.yuddy.luoning.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, UserService userService,
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Tu dang ky luon la hoc vien, tai khoan giang vien do admin cap
        User user = userService.createUser(request, Role.STUDENT);
        return toAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        // Cung mot thong bao cho ca 2 truong hop de khong lo email nao da ton tai
        User user = userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .orElseThrow(() -> ApiException.unauthorized("Email hoac mat khau khong dung"));

        // Kiem tra sau khi dung mat khau, de nguoi la khong do duoc tai khoan nao bi khoa
        if (user.isLocked()) {
            throw ApiException.forbidden("Tai khoan da bi khoa");
        }

        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(
                jwtService.generateToken(user),
                jwtService.getExpirationSeconds(),
                new UserResponse(user));
    }
}
