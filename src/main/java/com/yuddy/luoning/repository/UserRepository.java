package com.yuddy.luoning.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yuddy.luoning.entity.Role;
import com.yuddy.luoning.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByIdAndLockedTrue(Long id);

    Page<User> findAllBy(Pageable pageable);

    Page<User> findByRole(Role role, Pageable pageable);

}
