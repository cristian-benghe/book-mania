package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.models.BookModel;
import nl.tudelft.sem.template.example.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {
    private final transient BookService bookService;

    /**
     * Constructor for the BookController.
     *
     * @param bookService the BookService used by the controller
     */
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Add a book to the database. Only possible for users that are either authors or admins.
     *
     * @param id ID of the user
     * @param requestBody book (in JSON format) to be saved into the database
     * @return ResponseEntity with code 200 if successful or occurring error code
     */
    @PostMapping("/collection/{userID}")
    public ResponseEntity<Book> insert(@PathVariable("userID") Long id, @RequestBody BookModel requestBody) {
        //will test for the user to be an author/admin when the relevant endpoint becomes available

        if (requestBody == null) {
            return ResponseEntity.badRequest().build();
        }

        GenresConverter genresConverter = new GenresConverter();
        AuthorsConverter authorsConverter = new AuthorsConverter();
        try {
            Book book = new Book(
                    requestBody.getBookId(),
                    requestBody.getCreatorId(),
                    new Title(requestBody.getTitle()),
                    genresConverter.convertToEntityAttribute(requestBody.getGenres()),
                    authorsConverter.convertToEntityAttribute(requestBody.getAuthors()),
                    new NumPage(requestBody.getNumPage()));
            bookService.insert(book);
            return ResponseEntity.ok(book);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
