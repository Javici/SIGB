package com.library.sigb.security;

import com.library.sigb.entity.User;
import org.jspecify.annotations.Nullable;

/**
 * Contenedor del usuario autenticado mediante Scoped Values (JEP 487, Java 25).
 *
 * ScopedValue es la alternativa moderna a ThreadLocal en el contexto de
 * Virtual Threads: es inmutable dentro de su scope y se propaga
 * automáticamente a los threads hijos creados con Structured Concurrency,
 * eliminando el riesgo de fuga de contexto entre peticiones concurrentes
 * que sí existía con ThreadLocal.
 *
 * Uso:
 *   // En el filtro JWT, al inicio de cada petición:
 *   ScopedValue.runWhere(UserContext.CURRENT, user, () -> chain.doFilter(req, res));
 *
 *   // En cualquier punto de la cadena de llamada del mismo hilo:
 *   User me = UserContext.CURRENT.get();
 */
public final class UserContext {

    /**
     * Scoped Value que almacena el usuario autenticado para la petición actual.
     * Es null cuando la petición no está autenticada (rutas públicas).
     */
    public static final ScopedValue<@Nullable User> CURRENT = ScopedValue.newInstance();

    private UserContext() {}

    /** Obtiene el usuario actual sin lanzar excepción si no está disponible. */
    public static @Nullable User currentOrNull() {
        return CURRENT.isBound() ? CURRENT.get() : null;
    }
}
