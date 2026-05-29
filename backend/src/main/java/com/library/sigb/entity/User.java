package com.library.sigb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;

/**
 * Entidad que representa a un usuario del sistema.
 * Usa el namespace jakarta.* de Jakarta EE 11.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false, length = 50)
    @NonNull
    private String username;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false, length = 100)
    @NonNull
    private String email;

    @NotBlank
    @Column(nullable = false)
    @NonNull
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private Role role = Role.READER;

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Si no es null, el usuario no puede realizar préstamos ni reservas
     * hasta que esta fecha haya pasado (sanción por retrasos).
     */
    @Nullable
    private LocalDate sanctionedUntil;

    // ── Constructores ─────────────────────────────────────────────────

    public User() {}

    public User(@NonNull String username, @NonNull String email,
                @NonNull String password, @NonNull Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ── Getters / Setters ─────────────────────────────────────────────

    public Long getId() { return id; }

    public @NonNull String getUsername() { return username; }
    public void setUsername(@NonNull String username) { this.username = username; }

    public @NonNull String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }

    public @NonNull String getPassword() { return password; }
    public void setPassword(@NonNull String password) { this.password = password; }

    public @NonNull Role getRole() { return role; }
    public void setRole(@NonNull Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public @Nullable LocalDate getSanctionedUntil() { return sanctionedUntil; }
    public void setSanctionedUntil(@Nullable LocalDate sanctionedUntil) {
        this.sanctionedUntil = sanctionedUntil;
    }

    /** Comprueba si el usuario tiene una sanción vigente hoy. */
    public boolean isSanctioned() {
        return sanctionedUntil != null && !LocalDate.now().isAfter(sanctionedUntil);
    }
}
