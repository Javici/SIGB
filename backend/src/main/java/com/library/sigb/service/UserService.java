package com.library.sigb.service;

import com.library.sigb.dto.UserDto;
import com.library.sigb.entity.Role;
import com.library.sigb.entity.User;
import com.library.sigb.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Operaciones CRUD sobre usuarios.
 * Los métodos administrativos están protegidos con @PreAuthorize.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(@NonNull UserRepository userRepository,
                       @NonNull PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public @NonNull List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserDto::from).toList();
    }

    public @NonNull UserDto findById(Long id) {
        return UserDto.from(getOrThrow(id));
    }

    public @NonNull UserDto findByUsername(@NonNull String username) {
        return UserDto.from(userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Transactional
    public @NonNull UserDto updateProfile(Long id, @NonNull String email, @NonNull String newPassword) {
        User user = getOrThrow(id);
        if (!email.isBlank()) user.setEmail(email);
        if (!newPassword.isBlank()) user.setPassword(passwordEncoder.encode(newPassword));
        return UserDto.from(userRepository.save(user));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public @NonNull UserDto changeRole(Long id, @NonNull Role role) {
        User user = getOrThrow(id);
        user.setRole(role);
        return UserDto.from(userRepository.save(user));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public @NonNull UserDto setActive(Long id, boolean active) {
        User user = getOrThrow(id);
        user.setActive(active);
        return UserDto.from(userRepository.save(user));
    }

    // ── Helper ────────────────────────────────────────────────────────

    private @NonNull User getOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado: " + id));
    }
}
