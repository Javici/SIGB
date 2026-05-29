package com.library.sigb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Entidad que representa un título del catálogo de la biblioteca.
 * availableCopies se decrementa con cada préstamo activo y se incrementa
 * con cada devolución.
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    @NonNull
    private String title;

    @NotBlank
    @Column(nullable = false)
    @NonNull
    private String author;

    @Column(unique = true, length = 20)
    @Nullable
    private String isbn;

    @Column(length = 100)
    @Nullable
    private String category;

    @Column(length = 2000)
    @Nullable
    private String description;

    @PositiveOrZero
    @Column(nullable = false)
    private int totalCopies = 1;

    @PositiveOrZero
    @Column(nullable = false)
    private int availableCopies = 1;

    private int publishedYear;

    // ── Constructores ─────────────────────────────────────────────────

    public Book() {}

    public Book(@NonNull String title, @NonNull String author, int totalCopies) {
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    // ── Getters / Setters ─────────────────────────────────────────────

    public Long getId() { return id; }

    public @NonNull String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    public @NonNull String getAuthor() { return author; }
    public void setAuthor(@NonNull String author) { this.author = author; }

    public @Nullable String getIsbn() { return isbn; }
    public void setIsbn(@Nullable String isbn) { this.isbn = isbn; }

    public @Nullable String getCategory() { return category; }
    public void setCategory(@Nullable String category) { this.category = category; }

    public @Nullable String getDescription() { return description; }
    public void setDescription(@Nullable String description) { this.description = description; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public int getPublishedYear() { return publishedYear; }
    public void setPublishedYear(int publishedYear) { this.publishedYear = publishedYear; }

    public boolean isAvailable() { return availableCopies > 0; }
}
