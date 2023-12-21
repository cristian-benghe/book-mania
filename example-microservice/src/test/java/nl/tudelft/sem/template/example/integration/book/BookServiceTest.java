package nl.tudelft.sem.template.example.integration.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.builders.BookBuilder;
import nl.tudelft.sem.template.example.builders.BookDirector;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.dtos.BookRequest;
import nl.tudelft.sem.template.example.dtos.BookResponse;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BookServiceTest {

    @MockBean
    private BookRepository bookRepository;

    @Test
    public void addBookSuccessfully() {
        // create builder and director for sample book creation
        BookBuilder builder = new BookBuilder();
        BookDirector director = new BookDirector(builder);
        // construct the sample book
        director.constructValidBook();
        Book book = builder.build();
        // and mock the DB
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // set up test data
        long creatorId = 123L;
        BookRequest bookModel = new BookRequest();
        bookModel.setTitle("title");
        bookModel.setAuthor("author1,Author2");
        bookModel.setGenre("action,ADVENTURE");
        bookModel.setSeries("series");
        bookModel.setNumberOfPages(123);

        // get sample result from the service
        Book result = new BookService(bookRepository).insert(bookModel, creatorId);

        assertThat(result).isEqualTo(book);
    }
}
