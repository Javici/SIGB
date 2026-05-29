package com.library.sigb.dto;

import com.library.sigb.entity.Role;
import org.jspecify.annotations.NonNull;

/**
 * DTO de respuesta tras un login exitoso.
 * Contiene el JWT y los datos mínimos del usuario para el frontend.
 */
public record LoginResponse(
        @NonNull String token,
        @NonNull String username,
        @NonNull String email,
        @NonNull Role role
) {}
