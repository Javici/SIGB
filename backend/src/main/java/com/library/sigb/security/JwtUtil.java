package com.library.sigb.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Utilidad para generar y validar JSON Web Tokens (JWT).
 *
 * Se apoya en la librería jjwt 0.12.x, que es compatible con Jackson 3
 * (serialización JSON predeterminada de Spring Boot 4).
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.expiration}") long expirationMs) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    /** Genera un JWT con el username como subject y el rol como claim. */
    public @NonNull String generateToken(@NonNull String username, @NonNull String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    /** Extrae el username (subject) del token. */
    public @NonNull String extractUsername(@NonNull String token) {
        return getClaims(token).getSubject();
    }

    /** Extrae el rol del token. */
    public @NonNull String extractRole(@NonNull String token) {
        return getClaims(token).get("role", String.class);
    }

    /** Valida que el token no esté expirado y el subject coincida. */
    public boolean isTokenValid(@NonNull String token, @NonNull String username) {
        try {
            Claims claims = getClaims(token);
            return claims.getSubject().equals(username)
                    && !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(@NonNull String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
