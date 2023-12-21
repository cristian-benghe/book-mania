package nl.tudelft.sem.template.example.integration.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.dtos.BookContentResponse;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockBookService", "mockBookRepository"})
@AutoConfigureMockMvc
public class GetBookServiceTest {
    @Mock
    private BookRepository bookRepository;

    private BookService bookService;

    @BeforeEach
    public void setUp() {
        bookService = new BookService(bookRepository);
    }

    @Test
    public void getBookSuccessfullyTest() {
        Book book = new Book(
                1L,
                new Title("title"),
                new GenresConverter().convertToEntityAttribute("action"),
                new AuthorsConverter().convertToEntityAttribute("author"),
                new SeriesConverter().convertToEntityAttribute("series"),
                new NumPage(123)
        );

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookContentResponse response = bookService.getBook(1L);
        assertThat(response.getBook()).isEqualTo(book);
    }

    @Test
    public void bookNotFoundTest() {
        when(bookRepository.findById(1L)).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class,
                () -> bookService.getBook(1L));
    }
}