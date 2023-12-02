package nl.tudelft.sem.template.authentication.models.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private long bookId;
    private long creatorId;
    private TitleType title;
    private GenreType genres;
    private AuthorsType authors;
    private NumPageType numPages;
}
