package com.library.sigb.dto;

import com.library.sigb.entity.Reservation;
import com.library.sigb.entity.ReservationStatus;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;

/** DTO de respuesta para reservas. */
public record ReservationDto(
        @NonNull Long id,
        @NonNull Long userId,
        @NonNull String username,
        @NonNull Long bookId,
        @NonNull String bookTitle,
        @NonNull LocalDateTime reservationDate,
        @NonNull ReservationStatus status,
        int queuePosition
) {
    public static ReservationDto from(@NonNull Reservation r) {
        return new ReservationDto(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getUsername(),
                r.getBook().getId(),
                r.getBook().getTitle(),
                r.getReservationDate(),
                r.getStatus(),
                r.getQueuePosition()
        );
    }
}
