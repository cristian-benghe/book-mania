package nl.tudelft.sem.template.authentication.models.Book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private long bookID;
    private long creatorID;
    private TitleType title;
    private GenreType genres;
    private AuthorsType authors;
    private NumPageType numPages;
}
