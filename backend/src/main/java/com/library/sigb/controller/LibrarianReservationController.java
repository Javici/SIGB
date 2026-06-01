package com.library.sigb.controller;

import com.library.sigb.dto.LoanDto;
import com.library.sigb.dto.ReservationDto;
import com.library.sigb.service.ReservationService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de gestión de reservas para LIBRARIAN / ADMIN.
 *
 * GET  /api/v1/librarian/reservations/pending  – reservas pendientes de aprobación
 * PUT  /api/v1/librarian/reservations/{id}/accept – aceptar → crea préstamo
 * PUT  /api/v1/librarian/reservations/{id}/deny   – denegar → libera la copia si procede
 */
@RestController
@RequestMapping("/api/v1/librarian/reservations")
public class LibrarianReservationController {

    private final ReservationService reservationService;

    public LibrarianReservationController(@NonNull ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ReservationDto>> pending() {
        return ResponseEntity.ok(reservationService.findAllPending());
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<LoanDto> accept(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.acceptReservation(id));
    }

    @PutMapping("/{id}/deny")
    public ResponseEntity<Void> deny(@PathVariable Long id) {
        reservationService.denyReservation(id);
        return ResponseEntity.noContent().build();
    }
}
