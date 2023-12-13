package nl.tudelft.sem.template.example.integration.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.models.BookModel;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockBookRepository"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BookServiceTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void updateBookSuccessfully() {
        Book book = new Book();
        BookModel newBook = new BookModel();
        book.setTitle(new Title("InitialTitle"));
        newBook.setTitle("UpdatedTitle");
        Book updatedBook = new Book();
        updatedBook.setTitle(new Title("UpdatedTitle"));

        when(bookRepository.save(book)).thenReturn(book);

        Book results = new BookService(bookRepository).updateBook(book, newBook);
        assertThat(results).isEqualTo(updatedBook);
    }

    @Test
    public void addBookSuccessfully() {
        long creatorId = 123L;
        BookModel bookModel = new BookModel();
        bookModel.setTitle("title");
        bookModel.setAuthor("author1,Author2");
        bookModel.setGenre("action,ADVENTURE");
        bookModel.setSeries("series");
        bookModel.setNumberOfPages(123);

        Book book = new Book(
                creatorId,
                new Title(bookModel.getTitle()),
                new GenresConverter().convertToEntityAttribute(bookModel.getGenre()),
                new AuthorsConverter().convertToEntityAttribute(bookModel.getAuthor()),
                new SeriesConverter().convertToEntityAttribute(bookModel.getSeries()),
                new NumPage(bookModel.getNumberOfPages()));

        when(bookRepository.save(book)).thenReturn(book);

        Book result = new BookService(bookRepository).insert(bookModel, creatorId);
        assertThat(result).isEqualTo(book);
    }
}
