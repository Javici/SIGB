package com.library.sigb.controller;

import com.library.sigb.dto.LoanDto;
import com.library.sigb.service.LoanService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST de préstamos.
 *
 * GET  /api/v1/librarian/loans              – todos los préstamos (LIBRARIAN/ADMIN)
 * GET  /api/v1/librarian/loans/user/{id}    – préstamos de un usuario
 * POST /api/v1/librarian/loans              – crear préstamo
 * PUT  /api/v1/librarian/loans/{id}/return  – registrar devolución
 */
@RestController
@RequestMapping("/api/v1/librarian/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(@NonNull LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public ResponseEntity<List<LoanDto>> getAll() {
        return ResponseEntity.ok(loanService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.findByUser(userId));
    }

    @PostMapping
    public ResponseEntity<LoanDto> create(
            @RequestParam Long userId,
            @RequestParam Long bookId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.createLoan(userId, bookId));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<LoanDto> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnLoan(id));
    }
}
