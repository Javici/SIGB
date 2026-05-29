package com.library.sigb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NonNull;

/** DTO para registrar un nuevo usuario (POST /api/v1/auth/register). */
public record RegisterRequest(
        @NonNull @NotBlank @Size(min = 3, max = 50) String username,
        @NonNull @Email @NotBlank String email,
        @NonNull @NotBlank @Size(min = 6, max = 100) String password
) {}
