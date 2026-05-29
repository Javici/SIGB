package com.library.sigb.controller;

import com.library.sigb.dto.UserDto;
import com.library.sigb.entity.Role;
import com.library.sigb.security.UserContext;
import com.library.sigb.service.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de usuarios.
 *
 * GET  /api/v1/admin/users           – todos (ADMIN)
 * GET  /api/v1/users/me              – perfil propio
 * PUT  /api/v1/users/me              – actualizar perfil propio
 * PUT  /api/v1/admin/users/{id}/role – cambiar rol (ADMIN)
 * PUT  /api/v1/admin/users/{id}/active – activar/desactivar (ADMIN)
 */
@RestController
public class UserController {

    private final UserService userService;

    public UserController(@NonNull UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/v1/admin/users")
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /** Perfil del usuario autenticado, leído del Scoped Value (JEP 487). */
    @GetMapping("/api/v1/users/me")
    public ResponseEntity<UserDto> getMe() {
        var user = UserContext.currentOrNull();
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userService.findById(user.getId()));
    }

    @PutMapping("/api/v1/users/me")
    public ResponseEntity<UserDto> updateMe(
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "") String password) {
        var user = UserContext.currentOrNull();
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userService.updateProfile(user.getId(), email, password));
    }

    @PutMapping("/api/v1/admin/users/{id}/role")
    public ResponseEntity<UserDto> changeRole(
            @PathVariable Long id, @RequestParam Role role) {
        return ResponseEntity.ok(userService.changeRole(id, role));
    }

    @PutMapping("/api/v1/admin/users/{id}/active")
    public ResponseEntity<UserDto> setActive(
            @PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(userService.setActive(id, active));
    }
}
