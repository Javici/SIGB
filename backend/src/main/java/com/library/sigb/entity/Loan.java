package com.library.sigb.entity;

import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;

/**
 * Representa un préstamo físico de un ejemplar a un usuario.
 * El plazo estándar es de 14 días; los retrasos generan sanciones.
 */
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @NonNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    @NonNull
    private Book book;

    @Column(nullable = false)
    private LocalDate loanDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDate dueDate = LocalDate.now().plusDays(14);

    @Nullable
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private LoanStatus status = LoanStatus.ACTIVE;

    // ── Constructores ─────────────────────────────────────────────────

    public Loan() {}

    public Loan(@NonNull User user, @NonNull Book book) {
        this.user = user;
        this.book = book;
    }

    // ── Getters / Setters ─────────────────────────────────────────────

    public Long getId() { return id; }

    public @NonNull User getUser() { return user; }
    public void setUser(@NonNull User user) { this.user = user; }

    public @NonNull Book getBook() { return book; }
    public void setBook(@NonNull Book book) { this.book = book; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public @Nullable LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(@Nullable LocalDate returnDate) { this.returnDate = returnDate; }

    public @NonNull LoanStatus getStatus() { return status; }
    public void setStatus(@NonNull LoanStatus status) { this.status = status; }

    /**
     * Días de retraso respecto al dueDate (0 si no hay retraso).
     * Usa pattern matching sobre primitivos (JEP 507, Java 25 preview).
     */
    public long overdueDays() {
        LocalDate reference = (returnDate != null) ? returnDate : LocalDate.now();
        long days = dueDate.until(reference, java.time.temporal.ChronoUnit.DAYS);
        return Math.max(0L, days);
    }
}
