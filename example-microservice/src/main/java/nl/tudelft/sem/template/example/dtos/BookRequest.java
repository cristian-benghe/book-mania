package nl.tudelft.sem.template.example.dtos;

import lombok.Data;

/**
 * The DTO (Data Transfer Object) used for adding/editing a book to the database.
 */
@Data
public class BookRequest {
    private String title;
    private String author;
    private String genre;
    private String series;
    private Integer numberOfPages;
}
