package nl.tudelft.sem.template.example.controllers;

import java.util.NoSuchElementException;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.BookRequest;
import nl.tudelft.sem.template.example.dtos.BookResponse;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotAdminException;
import nl.tudelft.sem.template.example.exceptions.UserNotAuthorOfGivenBookException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.BookService;
import nl.tudelft.sem.template.example.validators.users.UserBannedValidator;
import nl.tudelft.sem.template.example.validators.users.UserBookValidator;
import nl.tudelft.sem.template.example.validators.users.UserNotAdminOrAuthorValidator;
import nl.tudelft.sem.template.example.validators.users.UserNotAdminValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<Object> addBook(@RequestParam("userID") Long creatorId,
                                          @RequestBody BookRequest requestBody) {

        if (requestBody == null || creatorId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userRepository.findById(creatorId).orElseThrow();

            if (user.getBanned().isBanned()) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("USER_BANNED"));
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
     * Update a book in the database. Only possible for users that are either admins or the authors of the book. From
     * the provided fields in the body, only the ones that are not null and have a valid format will be updated.
     *
     * @param requestBody book (in JSON format) to be updated in the database
     * @param userId ID of the user that made the request
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with the ID of the book</li>
     *         <li>ResponseEntity with code 403 if the user is not an admin, it is banned, or it is not the author
     *         of the book, along with the current state of the user</li>
     *         <li>ResponseEntity with code 404 if the book or user does not exist</li>
     *         <li>ResponseEntity with code 500 if other error occurred (e.g., server error, database error)</li>
     *     </ul>
     */
    @PutMapping("/collection")
    public ResponseEntity<Object> updateBook(@RequestBody BookRequest requestBody,
                                             @RequestParam("userID") Long userId,
                                             @RequestParam("bookID") Long bookId) {
        System.out.println("PUT /collection with request body " + requestBody + " and userID " + userId);

        if (requestBody == null || userId == null || bookId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();
            Book book = bookRepository.findById(bookId).orElseThrow();

            UserBookValidator userBannedValidator = new UserBannedValidator();
            userBannedValidator.setNext(new UserNotAdminOrAuthorValidator());
            userBannedValidator.handle(user, book);

            BookResponse response = bookService.updateBook(bookId, requestBody);
            if (response.getBookId() == null) {
                throw new IllegalArgumentException("Inconsistency in database!");
            } else {
                System.out.println("Updated book with ID " + response.getBookId());
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new BookResponse(response.getBookId()));
            }
        } catch (NoSuchElementException e) {
            System.out.println("Book or user not found!");
            return ResponseEntity.notFound().build();

        } catch (UserBannedException e) {
            System.out.println("User is banned!");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("USER_BANNED"));

        } catch (UserNotAuthorOfGivenBookException e) {
            System.out.println("User is an author, but not of the given book!");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("NOT_AN_AUTHOR"));
        } catch (UserNotAdminException e) {
            System.out.println("User is not an admin!");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("NOT_AN_ADMIN"));

        } catch (IllegalArgumentException e) {
            System.out.println("Inconsistency in database!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            System.out.println("Error when updating book!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a book from the database. Only possible for admins.
     *
     * @param userId ID of the user that made the request
     * @param bookId ID of the book to be deleted
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with the ID of the book</li>
     *         <li>ResponseEntity with code 403 if the user is not an admin or if it is banned,
     *         along with the current state of the </li>
     *         <li>ResponseEntity with code 404 if the book or user does not exist</li>
     *         <li>ResponseEntity with code 500 if other error occurred (e.g., server error, database error)</li>
     *     </ul>
     */
    @DeleteMapping("/collection")
    public ResponseEntity<Object> deleteBook(@RequestParam("userID") Long userId,
                                             @RequestParam("bookID") Long bookId) {
        System.out.println("DELETE /collection with userID " + userId + " and bookID " + bookId);

        if (userId == null || bookId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();

            UserBookValidator userBannedValidator = new UserBannedValidator();
            userBannedValidator.setNext(new UserNotAdminValidator());
            userBannedValidator.handle(user, null);

            BookResponse response = bookService.deleteBook(bookId);
            if (response.getBookId() == null) {
                throw new NoSuchElementException();
            } else {
                System.out.println("Deleted book with ID " + response.getBookId());
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new BookResponse(response.getBookId()));
            }
        } catch (NoSuchElementException e) {
            System.out.println("Book or user not found!");
            return ResponseEntity.notFound().build();

        } catch (UserBannedException e) {
            System.out.println("User is banned!");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("USER_BANNED"));

        } catch (UserNotAdminException e) {
            System.out.println("User is not an admin!");
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("NOT_AN_ADMIN"));

        } catch (Exception e) {
            System.out.println("Error when deleting book!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
