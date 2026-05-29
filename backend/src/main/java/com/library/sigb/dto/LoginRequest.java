package com.library.sigb.dto;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;

/**
 * DTO de entrada para el endpoint POST /api/v1/auth/login.
 * Usa Java 25 Records: inmutable, con equals/hashCode/toString generados.
 */
public record LoginRequest(
        @NonNull @NotBlank String username,
        @NonNull @NotBlank String password
) {}
