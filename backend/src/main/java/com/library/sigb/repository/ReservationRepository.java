package com.library.sigb.repository;

import com.library.sigb.entity.Reservation;
import com.library.sigb.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByBookIdAndStatusOrderByQueuePositionAsc(Long bookId, ReservationStatus status);

    Optional<Reservation> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, ReservationStatus status);

    List<Reservation> findAllByStatusOrderByReservationDateAsc(ReservationStatus status);

    /** Primer usuario en la cola de espera para un libro. */
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.book.id = :bookId AND r.status = 'PENDING'
        ORDER BY r.queuePosition ASC
        LIMIT 1
        """)
    Optional<Reservation> findFirstInQueue(@Param("bookId") Long bookId);

    /** Siguiente posición libre en la cola de un libro. */
    @Query("SELECT COALESCE(MAX(r.queuePosition), 0) + 1 FROM Reservation r WHERE r.book.id = :bookId AND r.status = 'PENDING'")
    int nextQueuePosition(@Param("bookId") Long bookId);
}
