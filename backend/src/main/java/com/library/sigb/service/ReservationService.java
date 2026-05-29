package com.library.sigb.service;

import com.library.sigb.dto.ReservationDto;
import com.library.sigb.entity.*;
import com.library.sigb.repository.BookRepository;
import com.library.sigb.repository.ReservationRepository;
import com.library.sigb.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Gestión de la cola de reservas.
 * El usuario reserva un libro cuando no hay ejemplares disponibles.
 * La posición en cola se asigna automáticamente.
 */
@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReservationService(@NonNull ReservationRepository reservationRepository,
                              @NonNull BookRepository bookRepository,
                              @NonNull UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public @NonNull List<ReservationDto> findByUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(ReservationDto::from).toList();
    }

    public @NonNull List<ReservationDto> findQueueForBook(Long bookId) {
        return reservationRepository
                .findByBookIdAndStatusOrderByQueuePositionAsc(bookId, ReservationStatus.PENDING)
                .stream().map(ReservationDto::from).toList();
    }

    @Transactional
    public @NonNull ReservationDto createReservation(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (user.isSanctioned()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Usuario sancionado hasta " + user.getSanctionedUntil());
        }
        if (book.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El libro está disponible; solicite un préstamo directamente.");
        }
        if (reservationRepository.findByUserIdAndBookIdAndStatus(
                userId, bookId, ReservationStatus.PENDING).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya tiene una reserva pendiente para este libro.");
        }

        int queuePos = reservationRepository.nextQueuePosition(bookId);
        Reservation reservation = new Reservation(user, book, queuePos);
        return ReservationDto.from(reservationRepository.save(reservation));
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long requestingUserId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Solo el propio usuario o un admin/bibliotecario puede cancelar
        if (!reservation.getUser().getId().equals(requestingUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No puede cancelar reservas de otros usuarios.");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }
}
