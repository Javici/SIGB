package com.library.sigb.service;

import com.library.sigb.dto.LoginRequest;
import com.library.sigb.dto.LoginResponse;
import com.library.sigb.dto.RegisterRequest;
import com.library.sigb.entity.Role;
import com.library.sigb.entity.User;
import com.library.sigb.repository.UserRepository;
import com.library.sigb.security.JwtUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servicio de autenticación: registro y login.
 * Devuelve un JWT firmado HS256 (jjwt 0.12.x).
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(@NonNull UserRepository userRepository,
                       @NonNull PasswordEncoder passwordEncoder,
                       @NonNull JwtUtil jwtUtil,
                       @NonNull AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public @NonNull LoginResponse register(@NonNull RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El nombre de usuario ya está en uso.");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El correo electrónico ya está registrado.");
        }

        User user = new User(
                req.username(),
                req.email(),
                passwordEncoder.encode(req.password()),
                Role.READER
        );
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    public @NonNull LoginResponse login(@NonNull LoginRequest req) {
        // Spring Security verifica username + password contra la BBDD y lanza excepción si falla
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getUsername(), user.getEmail(), user.getRole());
    }
}
