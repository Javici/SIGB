package com.library.sigb.controller;

import com.library.sigb.dto.ReservationDto;
import com.library.sigb.security.UserContext;
import com.library.sigb.service.ReservationService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de reservas.
 *
 * GET  /api/v1/reservations/my          – mis reservas (usuario autenticado)
 * GET  /api/v1/reservations/book/{id}   – cola de espera de un libro
 * POST /api/v1/reservations             – crear reserva
 * DELETE /api/v1/reservations/{id}      – cancelar reserva propia
 */
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(@NonNull ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /** Lee las reservas propias usando el usuario del Scoped Value (JEP 487). */
    @GetMapping("/my")
    public ResponseEntity<List<ReservationDto>> myReservations() {
        var user = UserContext.currentOrNull();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(reservationService.findByUser(user.getId()));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReservationDto>> queueForBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(reservationService.findQueueForBook(bookId));
    }

    @PostMapping
    public ResponseEntity<ReservationDto> create(
            @RequestParam Long bookId) {
        var user = UserContext.currentOrNull();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(user.getId(), bookId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        var user = UserContext.currentOrNull();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        reservationService.cancelReservation(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
