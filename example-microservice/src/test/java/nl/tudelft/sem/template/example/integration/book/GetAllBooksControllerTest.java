package nl.tudelft.sem.template.example.integration.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.builders.BookBuilder;
import nl.tudelft.sem.template.example.builders.BookDirector;
import nl.tudelft.sem.template.example.controllers.BookController;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockBookService", "mockBookRepository"})
@AutoConfigureMockMvc
public class GetAllBooksControllerTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    private BookController bookController;

    @BeforeEach
    public void setUp() {
        bookController = new BookController(new BookService(bookRepository), bookRepository, userRepository);
    }

    @Test
    public void getAllBooksSuccessfullyTest() {
        BookBuilder bookBuilder = new BookBuilder();
        BookDirector bookDirector = new BookDirector(bookBuilder);
        List<Book> expected = new ArrayList<>();

        bookDirector.constructValidBook();
        Book bookOne = bookBuilder.build();
        expected.add(bookOne);

        bookDirector.constructUpdatedBook();
        Book bookTwo = bookBuilder.build();
        expected.add(bookTwo);

        bookBuilder.setNumPages(256);
        bookBuilder.setCreator(18L);
        bookBuilder.setAuthors(List.of("author"));
        bookBuilder.setSeries(List.of("series", "SERIES"));
        Book bookThree = bookBuilder.build();
        expected.add(bookThree);

        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findAll()).thenReturn(expected);

        ResponseEntity<Object> response = bookController.getAllBooks(user.getUserId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void nullUserIdTest() {
        ResponseEntity<Object> response = bookController.getAllBooks(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void userNotFoundTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Object> response = bookController.getAllBooks(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    public void userBannedTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(true));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        ResponseEntity<Object> response = bookController.getAllBooks(user.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(new UserStatusResponse("USER_BANNED"));
    }

    @Test
    public void dbErrorTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findAll()).thenThrow(new RuntimeException());

        ResponseEntity<Object> response = bookController.getAllBooks(user.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("ERROR_WHEN_RETRIEVING_BOOKS");
    }
}
