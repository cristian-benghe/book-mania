package nl.tudelft.sem.template.example.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.example.domain.book.Book;

/**
 * The DTO (Data Transfer Object) used for returning multiple books at the same time.
 */
@Data
@AllArgsConstructor
public class BookListResponse {
    private List<Book> bookList;
}
