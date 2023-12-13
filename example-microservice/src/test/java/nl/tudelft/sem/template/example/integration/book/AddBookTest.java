package nl.tudelft.sem.template.example.integration.book;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.dtos.BookRequest;
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
@ActiveProfiles({"test", "mockBookService", "mockBookRepository"})
@AutoConfigureMockMvc
public class AddBookTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @Test
    public void addBookSuccessfully() throws Exception {
        Long creatorId = 123L;

        BookRequest requestBody = new BookRequest();
        requestBody.setTitle("title");
        requestBody.setAuthor("author1,Author2");
        requestBody.setGenre("action,ADVENTURE");
        requestBody.setSeries("series");
        requestBody.setNumberOfPages(123);

        Book book = new Book(
                creatorId,
                new Title(requestBody.getTitle()),
                new GenresConverter().convertToEntityAttribute(requestBody.getGenre()),
                new AuthorsConverter().convertToEntityAttribute(requestBody.getAuthor()),
                new SeriesConverter().convertToEntityAttribute(requestBody.getSeries()),
                new NumPage(requestBody.getNumberOfPages()));

        book.setBookId(73L);

        when(bookService.insert(requestBody, creatorId)).thenReturn(book);

        ResultActions result = mockMvc.perform(post("/api/collection")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestBody))
                .param("userID", String.valueOf(creatorId)));

        result.andExpect(status().isOk());

        String response = result.andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("{\"bookId\":73}");
    }
}
