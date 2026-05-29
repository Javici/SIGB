package com.library.sigb.dto;

import com.library.sigb.entity.Book;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * DTO bidireccional para el recurso Book.
 * Se usa tanto para recibir datos (creación/edición) como para enviar la respuesta.
 */
public record BookDto(
        @Nullable Long id,
        @NonNull @NotBlank String title,
        @NonNull @NotBlank String author,
        @Nullable String isbn,
        @Nullable String category,
        @Nullable String description,
        @PositiveOrZero int totalCopies,
        @PositiveOrZero int availableCopies,
        int publishedYear
) {
    /** Convierte la entidad Book a su DTO de respuesta. */
    public static BookDto from(@NonNull Book b) {
        return new BookDto(
                b.getId(), b.getTitle(), b.getAuthor(),
                b.getIsbn(), b.getCategory(), b.getDescription(),
                b.getTotalCopies(), b.getAvailableCopies(), b.getPublishedYear()
        );
    }

    /** Aplica los campos del DTO a una entidad Book existente o nueva. */
    public Book toEntity() {
        Book b = new Book();
        b.setTitle(title);
        b.setAuthor(author);
        b.setIsbn(isbn);
        b.setCategory(category);
        b.setDescription(description);
        b.setTotalCopies(totalCopies);
        b.setAvailableCopies(availableCopies);
        b.setPublishedYear(publishedYear);
        return b;
    }
}
