package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.domain.book.Book;
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
     * @param book book to be saved into the database
     * @return ResponseEntity with code 200 if successful or occurring error code
     */
    @PostMapping("/collection/{userID}")
    public ResponseEntity<Book> insert(@PathVariable("userID") Long id, @RequestBody Book book) {
        //will test for the user to be an author/admin when the relevant endpoint becomes available
        try {
            bookService.insert(book);
            return ResponseEntity.ok(book);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
