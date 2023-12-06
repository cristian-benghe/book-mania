package nl.tudelft.sem.template.example.models;

import lombok.Data;
import nl.tudelft.sem.template.example.domain.book.Title;

/**
 * Model represented a book.
 */
@Data
public class BookModel {
    private long bookId;
    private long creatorId;
    private String title;
    private String genres;
    private String authors;
    private int numPage;
}
