package nl.tudelft.sem.template.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The DTO (Data Transfer Object) used for returning the bookId of the book
 * on which an action was performed successfully.
 */
@Data
@AllArgsConstructor
public class BookResponse {
    private Long bookId;
}
