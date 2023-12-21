package nl.tudelft.sem.template.example.builders;

import java.util.List;
import lombok.Setter;
import nl.tudelft.sem.template.example.domain.book.Genre;

@Setter
public class BookDirector {
    private BookBuilderInterface bookBuilder;

    /**
     * Sets up the builder with the fields necessary to construct
     * a valid book object.
     */
    public void constructValidBook() {
        this.bookBuilder
            .setCreator(123L)
            .setTitle("title")
            .setAuthors(List.of("author1", "author2"))
            .setGenres(List.of(Genre.ACTION, Genre.ADVENTURE))
            .setSeries(List.of("series"))
            .setNumPages(123);
    }
}
