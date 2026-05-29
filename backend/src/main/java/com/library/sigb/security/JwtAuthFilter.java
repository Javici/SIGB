package com.library.sigb.security;

import com.library.sigb.entity.User;
import com.library.sigb.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro JWT que se ejecuta una vez por petición.
 *
 * Flujo:
 * 1. Extrae el token del encabezado Authorization: Bearer <token>
 * 2. Valida el token con JwtUtil
 * 3. Carga el User desde la BD y establece el contexto de seguridad
 * 4. Propaga el User al Scoped Value UserContext.CURRENT para toda
 *    la cadena de ejecución del Virtual Thread actual (JEP 487)
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthFilter(@NonNull JwtUtil jwtUtil,
                         @NonNull UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            chain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null
                && jwtUtil.isTokenValid(token, username)) {

            User user = userRepository.findByUsername(username).orElse(null);

            if (user != null && user.isActive()) {
                String role = "ROLE_" + user.getRole().name();
                var auth = new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Propagar el User autenticado al Scoped Value del Virtual Thread actual.
                // El ScopedValue.runWhere() crea un scope léxico: CURRENT está disponible
                // en chain.doFilter() y en todos los hilos hijos de Structured Concurrency.
                try {
                    ScopedValue.where(UserContext.CURRENT, user).run(
                            () -> {
                                try {
                                    chain.doFilter(request, response);
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            });
                } catch (RuntimeException ex) {
                    if (ex.getCause() instanceof IOException ioEx) throw ioEx;
                    if (ex.getCause() instanceof ServletException sEx) throw sEx;
                    throw ex;
                }
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
