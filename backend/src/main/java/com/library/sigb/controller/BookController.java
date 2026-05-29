package com.library.sigb.controller;

import com.library.sigb.dto.BookDto;
import com.library.sigb.service.BookService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST del catálogo de libros.
 *
 * GET  /api/v1/books           – listado completo (público)
 * GET  /api/v1/books/search    – búsqueda multifiltro (público)
 * GET  /api/v1/books/{id}      – detalle (público)
 * POST /api/v1/books           – crear (LIBRARIAN/ADMIN)
 * PUT  /api/v1/books/{id}      – actualizar (LIBRARIAN/ADMIN)
 * DELETE /api/v1/books/{id}    – eliminar (ADMIN)
 */
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(@NonNull BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAll() {
        return ResponseEntity.ok(bookService.findAll());
    }

    /** Búsqueda avanzada multifiltro. Todos los parámetros son opcionales. */
    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> search(
            @RequestParam @Nullable String title,
            @RequestParam @Nullable String author,
            @RequestParam @Nullable String isbn,
            @RequestParam @Nullable String category,
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        return ResponseEntity.ok(bookService.search(title, author, isbn, category, availableOnly));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BookDto> create(@Valid @RequestBody BookDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> update(@PathVariable Long id,
                                          @Valid @RequestBody BookDto dto) {
        return ResponseEntity.ok(bookService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
