package nl.tudelft.sem.template.example.integration.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.models.BookModel;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockBookService", "mockBookRepository", "mockUserRepository"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UpdateBookTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    public void updateBookSuccessfully() throws Exception {
        long userId = 1L;
        long bookId = 5L;
        User user = new User();
        user.setUserId(userId);
        user.setRole(new UserEnumType("ADMIN"));
        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle(new Title("InitialTitle"));
        Book newBook = new Book();
        newBook.setBookId(bookId);
        newBook.setTitle(new Title("UpdatedTitle"));

        BookModel requestBody = new BookModel();
        requestBody.setTitle("UpdatedTitle");

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(java.util.Optional.of(book));
        when(bookService.updateBook(book, requestBody)).thenReturn(newBook);

        ResultActions result = mockMvc.perform(put("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody))
                        .param("bookID", String.valueOf(bookId))
                        .param("userID", String.valueOf(userId)));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("{\"bookId\":5}");
    }
}
