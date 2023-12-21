package nl.tudelft.sem.template.example.integration.book;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.template.example.controllers.BookController;
import nl.tudelft.sem.template.example.dtos.BookRequest;
import nl.tudelft.sem.template.example.dtos.BookResponse;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
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
public class AddBookControllerTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookService bookService;

    private BookController bookController;

    @BeforeEach
    public void setUp() {
        bookController = new BookController(bookService, bookRepository, userRepository);
    }

    @Test
    public void addBookSuccessfullyTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));

        BookRequest requestBody = new BookRequest();
        requestBody.setTitle("title");
        requestBody.setAuthor("author1,Author2");
        requestBody.setGenre("action,ADVENTURE");
        requestBody.setSeries("series");
        requestBody.setNumberOfPages(123);

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookService.addBook(user.getUserId(), requestBody)).thenReturn(new BookResponse(2L));

        ResponseEntity<Object> response = bookController.addBook(user.getUserId(), requestBody);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new BookResponse(2L));
    }

    @Test
    public void nullBodyTest() {
        ResponseEntity<Object> response = bookController.addBook(1L, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void nullIdTest() {
        BookRequest request = new BookRequest();
        ResponseEntity<Object> response = bookController.addBook(null, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void userNotFoundTest() {
        BookRequest requestBody = new BookRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bookController.addBook(1L, requestBody);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void userBannedTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(true));
        user.setRole(new UserEnumType("ADMIN"));

        BookRequest requestBody = new BookRequest();
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookService.addBook(user.getUserId(), requestBody)).thenReturn(new BookResponse(2L));

        ResponseEntity<Object> response = bookController.addBook(user.getUserId(), requestBody);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(new UserStatusResponse("USER_BANNED"));
    }

    @Test
    public void regularUserTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("USER"));

        BookRequest requestBody = new BookRequest();
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookService.addBook(user.getUserId(), requestBody)).thenReturn(new BookResponse(2L));

        ResponseEntity<Object> response = bookController.addBook(user.getUserId(), requestBody);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(new UserStatusResponse("NOT_ADMIN_OR_AUTHOR"));
    }

    @Test
    public void databaseErrorTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookService.addBook(user.getUserId(), new BookRequest())).thenThrow(new RuntimeException());

        ResponseEntity<Object> response = bookController.addBook(user.getUserId(), new BookRequest());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
