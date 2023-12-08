package nl.tudelft.sem.template.example.models;

import lombok.Data;

/**
 * Model represented a book.
 */
@Data
public class BookModel {
    private String title;
    private String author;
    private String genre;
    private String series;
    private Integer numberOfPages;
}
