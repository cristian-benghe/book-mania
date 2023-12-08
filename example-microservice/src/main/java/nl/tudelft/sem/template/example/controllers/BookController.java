package nl.tudelft.sem.template.example.controllers;

import java.util.HashMap;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.models.BookModel;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BookController {
    private final transient BookService bookService;
    private final transient BookRepository bookRepository;
    private final transient UserRepository userRepository;

    /**
     * Constructor for the BookController.
     *
     * @param bookService the BookService used by the controller
     * @param bookRepository the BookRepository used by the controller
     * @param userRepository the UserRepository used by the controller
     */
    public BookController(BookService bookService, BookRepository bookRepository, UserRepository userRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Add a book to the database. Only possible for users that are either authors or admins.
     *
     * @param creatorId ID of the user that created the book
     * @param requestBody book (in JSON format) to be saved into the database
     * @return ResponseEntity with code 200 if successful or occurring error code
     */
    @PostMapping("/collection")
    public ResponseEntity<Book> insert(@RequestParam("userID") Long creatorId, @RequestBody BookModel requestBody) {
        //TODO will test for the user to be an author/admin when the relevant endpoint becomes available

        if (requestBody == null || creatorId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Book book = bookService.insert(requestBody, creatorId);
            System.out.println("Added book: " + book);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    /**
     * Update a book in the database. Only possible for users that are either authors or admins.
     *
     * @param requestBody book (in JSON format) to be updated in the database
     * @param userId ID of the user that made the request
     * @return ResponseEntity with code 200 if successful or occurring error code
     */
    @PutMapping("/collection")
    public ResponseEntity<Object> updateBook(@RequestBody BookModel requestBody,
                                             @RequestParam("userID") Long userId,
                                             @RequestParam("bookID") Long bookId) {
        System.out.println("PUT /collection with request body " + requestBody + " and userID " + userId);

        if (requestBody == null || userId == null || bookId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();

            //TODO check if user is an author or admin
            if (! (user.getRole().getUserRole().equals("ADMIN")
                    || user.getRole().getUserRole().equals("AUTHOR"))) {
                System.out.println("User is not an author or admin!");
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new HashMap<>() {{
                                put("role", "NOT_ADMIN");
                            }
                        });
            }

            Book book = bookRepository.findById(bookId).orElseThrow();
            Book newBook = bookService.updateBook(book, requestBody);
            System.out.println("Updated book: " + newBook);

        } catch (NoSuchElementException e) {
            System.out.println("Book or user not found!");
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            System.out.println("Error when updating book!");
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new HashMap<>() {{
                put("bookId", bookId);
            }
        });
    }
}
