package com.library.sigb.dto;

import com.library.sigb.entity.Role;
import com.library.sigb.entity.User;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;

/**
 * DTO de respuesta para el recurso User.
 * Nunca expone el campo password.
 */
public record UserDto(
        @NonNull Long id,
        @NonNull String username,
        @NonNull String email,
        @NonNull Role role,
        boolean active,
        boolean sanctioned,
        @Nullable LocalDate sanctionedUntil
) {
    public static UserDto from(@NonNull User u) {
        return new UserDto(
                u.getId(), u.getUsername(), u.getEmail(),
                u.getRole(), u.isActive(),
                u.isSanctioned(), u.getSanctionedUntil()
        );
    }
}
