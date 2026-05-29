package com.library.sigb.entity;

import jakarta.persistence.*;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;

/**
 * Cola de espera: un usuario reserva un libro cuando no hay ejemplares disponibles.
 * queuePosition determina el orden de notificación al devolver el libro.
 */
@Entity
@Table(name = "reservations")
public class Reservation {

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
    private LocalDateTime reservationDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(nullable = false)
    private int queuePosition;

    // ── Constructores ─────────────────────────────────────────────────

    public Reservation() {}

    public Reservation(@NonNull User user, @NonNull Book book, int queuePosition) {
        this.user = user;
        this.book = book;
        this.queuePosition = queuePosition;
    }

    // ── Getters / Setters ─────────────────────────────────────────────

    public Long getId() { return id; }

    public @NonNull User getUser() { return user; }
    public void setUser(@NonNull User user) { this.user = user; }

    public @NonNull Book getBook() { return book; }
    public void setBook(@NonNull Book book) { this.book = book; }

    public LocalDateTime getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public @NonNull ReservationStatus getStatus() { return status; }
    public void setStatus(@NonNull ReservationStatus status) { this.status = status; }

    public int getQueuePosition() { return queuePosition; }
    public void setQueuePosition(int queuePosition) { this.queuePosition = queuePosition; }
}
