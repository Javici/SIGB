package com.library.sigb.entity;

/** Estado del ciclo de vida de un préstamo físico. */
public enum LoanStatus {
    /** El libro está prestado y dentro del plazo. */
    ACTIVE,
    /** Devuelto a tiempo o con retraso (retraso registrado como sanción). */
    RETURNED,
    /** Plazo vencido sin devolución. */
    OVERDUE
}
