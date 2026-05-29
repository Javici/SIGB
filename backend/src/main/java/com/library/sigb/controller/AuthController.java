package com.library.sigb.controller;

import com.library.sigb.dto.LoginRequest;
import com.library.sigb.dto.LoginResponse;
import com.library.sigb.dto.RegisterRequest;
import com.library.sigb.service.AuthService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints públicos de autenticación.
 * Versión de API: /api/v1  (Spring MVC API Versioning nativo de Spring Boot 4)
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(@NonNull AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/v1/auth/register – Crea una nueva cuenta con rol READER. */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    /** POST /api/v1/auth/login – Devuelve un JWT si las credenciales son correctas. */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
