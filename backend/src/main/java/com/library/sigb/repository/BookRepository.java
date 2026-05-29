package com.library.sigb.repository;

import com.library.sigb.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    /** Búsqueda avanzada multifiltro: title, author, isbn, category, disponibilidad. */
    @Query("""
        SELECT b FROM Book b
        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))
          AND (:isbn IS NULL OR b.isbn = :isbn)
          AND (:category IS NULL OR LOWER(b.category) LIKE LOWER(CONCAT('%', :category, '%')))
          AND (:availableOnly = false OR b.availableCopies > 0)
        ORDER BY b.title ASC
        """)
    List<Book> searchBooks(
            @Param("title") String title,
            @Param("author") String author,
            @Param("isbn") String isbn,
            @Param("category") String category,
            @Param("availableOnly") boolean availableOnly
    );
}
