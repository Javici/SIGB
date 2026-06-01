package com.library.sigb.service;

import com.library.sigb.dto.LoanDto;
import com.library.sigb.dto.ReservationDto;
import com.library.sigb.entity.*;
import com.library.sigb.repository.BookRepository;
import com.library.sigb.repository.LoanRepository;
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
    private final LoanRepository loanRepository;

    public ReservationService(@NonNull ReservationRepository reservationRepository,
                              @NonNull BookRepository bookRepository,
                              @NonNull UserRepository userRepository,
                              @NonNull LoanRepository loanRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    public @NonNull List<ReservationDto> findByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return reservationRepository.findByUserId(user.getId()).stream()
                .map(ReservationDto::from).toList();
    }

    public @NonNull List<ReservationDto> findQueueForBook(Long bookId) {
        return reservationRepository
                .findByBookIdAndStatusOrderByQueuePositionAsc(bookId, ReservationStatus.PENDING)
                .stream().map(ReservationDto::from).toList();
    }

    @Transactional
    public @NonNull ReservationDto createReservation(String username, Long bookId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (user.isSanctioned()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Usuario sancionado hasta " + user.getSanctionedUntil());
        }
        if (reservationRepository.findByUserIdAndBookIdAndStatus(
                user.getId(), bookId, ReservationStatus.PENDING).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya tiene una reserva pendiente para este libro.");
        }

        // Si hay ejemplares disponibles, reservar uno directamente
        boolean held = book.isAvailable();
        if (held) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
        }

        int queuePos = reservationRepository.nextQueuePosition(bookId);
        Reservation reservation = new Reservation(user, book, queuePos);
        reservation.setHeldCopy(held);
        return ReservationDto.from(reservationRepository.save(reservation));
    }

    public @NonNull List<ReservationDto> findAllPending() {
        return reservationRepository
                .findAllByStatusOrderByReservationDateAsc(ReservationStatus.PENDING)
                .stream().map(ReservationDto::from).toList();
    }

    @Transactional
    public @NonNull LoanDto acceptReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La reserva no está pendiente.");
        }

        // La copia ya fue decrementada al crear la reserva; solo creamos el préstamo
        Loan loan = new Loan(reservation.getUser(), reservation.getBook());
        loanRepository.save(loan);

        reservation.setStatus(ReservationStatus.FULFILLED);
        reservationRepository.save(reservation);

        return LoanDto.from(loan);
    }

    @Transactional
    public void denyReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La reserva no está pendiente.");
        }

        // Si se había reservado una copia disponible, devolverla al catálogo
        if (reservation.isHeldCopy()) {
            Book book = reservation.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(Long reservationId, String requestingUsername) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!reservation.getUser().getUsername().equals(requestingUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No puede cancelar reservas de otros usuarios.");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }
}
