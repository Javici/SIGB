package com.library.sigb.repository;

import com.library.sigb.entity.Loan;
import com.library.sigb.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserId(Long userId);

    List<Loan> findByBookId(Long bookId);

    List<Loan> findByStatus(LoanStatus status);

    /** Préstamos activos vencidos (para proceso nocturno de sanciones). */
    List<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDate date);

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, LoanStatus status);
}
