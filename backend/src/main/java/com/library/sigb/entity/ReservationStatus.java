package com.library.sigb.entity;

/** Estado de una reserva en la cola de espera. */
public enum ReservationStatus {
    /** En cola; el ejemplar aún no está disponible. */
    PENDING,
    /** El sistema ha notificado al usuario que hay ejemplar disponible. */
    NOTIFIED,
    /** El usuario recogió el libro (se convirtió en préstamo). */
    FULFILLED,
    /** Cancelada por el usuario o caducada. */
    CANCELLED
}
