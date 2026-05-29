package com.library.sigb.service;

import com.library.sigb.dto.LoanDto;
import com.library.sigb.entity.*;
import com.library.sigb.repository.BookRepository;
import com.library.sigb.repository.LoanRepository;
import com.library.sigb.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de préstamos físicos.
 *
 * Al registrar una devolución:
 *  – Libera el ejemplar (availableCopies++)
 *  – Calcula la multa con pattern matching sobre long (JEP 507, Java 25)
 *  – Aplica sanción al usuario si hay retraso
 *  – Delega a NotificationService la notificación asíncrona al siguiente en cola
 */
@Service
@Transactional(readOnly = true)
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public LoanService(@NonNull LoanRepository loanRepository,
                       @NonNull BookRepository bookRepository,
                       @NonNull UserRepository userRepository,
                       @NonNull NotificationService notificationService) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public @NonNull List<LoanDto> findAll() {
        return loanRepository.findAll().stream().map(LoanDto::from).toList();
    }

    public @NonNull List<LoanDto> findByUser(Long userId) {
        return loanRepository.findByUserId(userId).stream().map(LoanDto::from).toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public @NonNull LoanDto createLoan(Long userId, Long bookId) {
        User user = getUserOrThrow(userId);
        Book book = getBookOrThrow(bookId);

        // Validaciones de negocio
        if (user.isSanctioned()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El usuario tiene una sanción activa hasta " + user.getSanctionedUntil());
        }
        if (!book.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No hay ejemplares disponibles. Puede realizar una reserva.");
        }
        if (loanRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, LoanStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El usuario ya tiene este libro en préstamo.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Loan loan = new Loan(user, book);
        return LoanDto.from(loanRepository.save(loan));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public @NonNull LoanDto returnLoan(Long loanId) {
        Loan loan = getLoanOrThrow(loanId);

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El préstamo ya fue devuelto o no está activo.");
        }

        loan.setReturnDate(LocalDate.now());

        // Calcular días de retraso y aplicar sanción si procede
        long overdue = loan.overdueDays();
        applySanctionIfNeeded(loan.getUser(), overdue);

        loan.setStatus(overdue > 0 ? LoanStatus.OVERDUE : LoanStatus.RETURNED);

        // Liberar ejemplar
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        loanRepository.save(loan);

        // Notificación asíncrona al siguiente en cola (Structured Concurrency + Virtual Threads)
        notificationService.notifyNextInQueue(loan);

        return LoanDto.from(loan);
    }

    /**
     * Aplica sanción al usuario según días de retraso.
     * Usa pattern matching sobre long (JEP 507 - Java 25 preview).
     *
     * La lógica de negocio:
     *  1–7 días  → sanción de 3 días
     *  8–14 días → sanción de 7 días
     *  >14 días  → sanción de 30 días
     */
    private void applySanctionIfNeeded(@NonNull User user, long overdueDays) {
        if (overdueDays <= 0) return;

        int d = (int) Math.min(overdueDays, Integer.MAX_VALUE);
        int sanctionDays = d <= 7 ? 3 : d <= 14 ? 7 : 30;

        LocalDate newSanctionEnd = LocalDate.now().plusDays(sanctionDays);
        // Si ya tenía sanción, extender la más larga
        if (user.getSanctionedUntil() == null || user.getSanctionedUntil().isBefore(newSanctionEnd)) {
            user.setSanctionedUntil(newSanctionEnd);
            userRepository.save(user);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private @NonNull User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuario no encontrado: " + id));
    }

    private @NonNull Book getBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Libro no encontrado: " + id));
    }

    private @NonNull Loan getLoanOrThrow(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Préstamo no encontrado: " + id));
    }
}
