package nl.tudelft.sem.template.example.domain.book;

import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.NumPageConverter;
import nl.tudelft.sem.template.example.domain.book.converters.TitleConverter;

/**
 * A DDD entity representing a book in our domain.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {
    /**
     * Identifier for the book.
     */
    @Id
    private long bookId;

    private long creatorId;

    @Embedded
    @Convert(converter = TitleConverter.class, attributeName = "bookTitle")
    private Title title;

    @Embedded
    @Convert(converter = GenresConverter.class, attributeName = "genreList")
    private Genres genres;

    @Embedded
    @Convert(converter = AuthorsConverter.class, attributeName = "listAuthors")
    private Authors authors;

    @Embedded
    @Convert(converter = NumPageConverter.class, attributeName = "numPages")
    private NumPage numPages;
}
