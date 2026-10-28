package com.accenture.externalapis.demo.dto;

/**
 * Raw JSON returned by the external book service for GET /api/books/{id}.
 * Mirrors the external contract exactly; BookDto keeps only what this app needs.
 *
 * @author Karyna Litvinova
 */
public record BookApiResponse(Long id,
                              String title,
                              String author,
                              String genre,
                              double price,
                              String isbn,
                              int publishedYear) {
}