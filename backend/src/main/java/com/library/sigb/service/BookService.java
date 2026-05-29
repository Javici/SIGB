package com.library.sigb.service;

import com.library.sigb.dto.BookDto;
import com.library.sigb.entity.Book;
import com.library.sigb.repository.BookRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Servicio de catálogo de libros.
 *
 * La búsqueda avanzada acepta cualquier combinación de los cinco filtros:
 * title, author, isbn, category y availableOnly. Si un campo es null o
 * vacío se ignora en la consulta JPQL de BookRepository.
 */
@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public BookService(@NonNull BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /** Búsqueda multifiltro (motor de búsqueda avanzada). */
    public @NonNull List<BookDto> search(
            @Nullable String title,
            @Nullable String author,
            @Nullable String isbn,
            @Nullable String category,
            boolean availableOnly) {
        return bookRepository.searchBooks(
                        nullIfBlank(title),
                        nullIfBlank(author),
                        nullIfBlank(isbn),
                        nullIfBlank(category),
                        availableOnly)
                .stream()
                .map(BookDto::from)
                .toList();
    }

    public @NonNull List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(BookDto::from).toList();
    }

    public @NonNull BookDto findById(Long id) {
        return BookDto.from(getOrThrow(id));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public @NonNull BookDto create(@NonNull BookDto dto) {
        Book book = dto.toEntity();
        return BookDto.from(bookRepository.save(book));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public @NonNull BookDto update(Long id, @NonNull BookDto dto) {
        Book book = getOrThrow(id);
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setIsbn(dto.isbn());
        book.setCategory(dto.category());
        book.setDescription(dto.description());
        book.setTotalCopies(dto.totalCopies());
        book.setPublishedYear(dto.publishedYear());
        return BookDto.from(bookRepository.save(book));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado.");
        }
        bookRepository.deleteById(id);
    }

    // ── Helper ────────────────────────────────────────────────────────

    private @NonNull Book getOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Libro no encontrado: " + id));
    }

    private @Nullable String nullIfBlank(@Nullable String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
