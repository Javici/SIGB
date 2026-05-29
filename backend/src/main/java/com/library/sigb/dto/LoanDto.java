package com.library.sigb.dto;

import com.library.sigb.entity.Loan;
import com.library.sigb.entity.LoanStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;

/** DTO de respuesta para préstamos. */
public record LoanDto(
        @NonNull Long id,
        @NonNull Long userId,
        @NonNull String username,
        @NonNull Long bookId,
        @NonNull String bookTitle,
        @NonNull LocalDate loanDate,
        @NonNull LocalDate dueDate,
        @Nullable LocalDate returnDate,
        @NonNull LoanStatus status,
        long overdueDays,
        double fine
) {
    public static LoanDto from(@NonNull Loan l) {
        long overdue = l.overdueDays();
        return new LoanDto(
                l.getId(),
                l.getUser().getId(),
                l.getUser().getUsername(),
                l.getBook().getId(),
                l.getBook().getTitle(),
                l.getLoanDate(),
                l.getDueDate(),
                l.getReturnDate(),
                l.getStatus(),
                overdue,
                calculateFine(overdue)
        );
    }

    /**
     * Cálculo de multa por retraso.
     * Usa switch expression con pattern matching sobre long (JEP 507, Java 25 preview).
     *   0 días             → sin multa
     *   1-7 días           → 0.50 € / día
     *   8-30 días          → 3.50 € + 1.00 € / día extra
     *   > 30 días          → 26.50 € + 2.00 € / día extra
     */
    private static double calculateFine(long days) {
        if (days <= 0) return 0.0;
        if (days <= 7) return days * 0.50;
        if (days <= 30) return 3.50 + (days - 7) * 1.00;
        return 3.50 + 23.00 + (days - 30) * 2.00;
    }
}
