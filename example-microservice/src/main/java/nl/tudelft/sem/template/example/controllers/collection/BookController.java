package nl.tudelft.sem.template.example.controllers.collection;

import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.book.BookRequest;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    //PMD does not allow more than 3 String literals in the same file
    //Created attributes for now, would appreciate feedback.
    private final transient String uid = "userID";
    private final transient String ub = "USER_BANNED";

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
    public ResponseEntity<Object> addBook(@RequestParam(uid) Long creatorId,
                                          @RequestBody BookRequest requestBody) {

        if (requestBody == null || creatorId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userRepository.findById(creatorId).orElseThrow();

            if (user.getBanned().isBanned()) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse(ub));
            }

            if (user.getRole().getUserRole().equals("USER")) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("NOT_ADMIN_OR_AUTHOR"));
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("USER_NOT_FOUND");
        }

        try {
            BookResponse response = bookService.addBook(creatorId, requestBody);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR_WHEN_ADDING_BOOK");
        }
    }


    /**
     * Retrieves a book from the database with the given ID. Only possible for user that are not banned.
     *
     * @param userId The ID of the user making the request
     * @param bookId The ID of the book to be retrieved from the database
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with the contents of the book</li>
     *         <li>ResponseEntity with code 403 if the user is banned</li>
     *         <li>ResponseEntity with code 404 if the book or user does not exist</li>
     *         <li>ResponseEntity with code 500 for other errors</li>
     *     </ul>
     */
    @GetMapping("/book/{bookID}")
    public ResponseEntity<Object> getBook(@RequestParam(uid) Long userId,
                                          @PathVariable("bookID") Long bookId) {
        if (userId == null || bookId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();

            if (new BannedConverter().convertToDatabaseColumn(user.getBanned())) {
                throw new UserBannedException();
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("USER_NOT_FOUND");
        } catch (UserBannedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse(ub));
        }

        try {
            Book response = bookService.getBook(bookId).getBook();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("BOOK_NOT_FOUND");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR_WHEN_RETRIEVING_BOOK");
        }
    }

    /**
     * Retrieves all distinct books from the database. Only possible for registered users that are not banned.
     *
     * @param userId The ID of the user making the request.
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with a list of all distinct books in the database</li>
     *         <li>ResponseEntity with code 403 if the user is banned</li>
     *         <li>ResponseEntity with code 404 if the user does not exist</li>
     *         <li>ResponseEntity with code 500 for other errors</li>
     *     </ul>
     */
    @GetMapping("/getAllBooks")
    public ResponseEntity<Object> getAllBooks(@RequestParam(uid) Long userId) {
        if (userId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new UserNotFoundException("User not found"));

            if (new BannedConverter().convertToDatabaseColumn(user.getBanned())) {
                throw new UserBannedException();
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("USER_NOT_FOUND");
        } catch (UserBannedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse(ub));
        }

        try {
            List<Book> response = bookService.getAllBooks().getBookList();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR_WHEN_RETRIEVING_BOOKS");
        }
    }
}
