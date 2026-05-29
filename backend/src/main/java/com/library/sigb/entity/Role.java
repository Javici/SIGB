package com.library.sigb.entity;

/**
 * Roles del sistema (multi-usuario).
 * ADMIN       – control total: gestiona empleados y configuración global.
 * LIBRARIAN   – gestiona catálogo, préstamos y devoluciones.
 * READER      – busca libros, reserva y consulta su historial.
 */
public enum Role {
    ADMIN,
    LIBRARIAN,
    READER
}
