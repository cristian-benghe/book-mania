package nl.tudelft.sem.template.example.controllers.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.book.BookDetailsResponse;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccessCollectionController {
    private final transient BookService bookService;
    private final transient UserRepository userRepository;

    /**
     * Constructor for the BookController.
     *
     * @param bookService the BookService used by the controller
     * @param userRepository the UserRepository used by the controller
     */
    public AccessCollectionController(BookService bookService, UserRepository userRepository) {
        this.bookService = bookService;
        this.userRepository = userRepository;
    }

    /**
     * Get all books by a given author.
     *
     * @param authorId the id of the author
     * @param userId the id of the user that made the request
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with the list of specific books</li>
     *         <li>ResponseEntity with code 403 if the user that made a request is banned or
     *         the provided user is not an author</li>
     *         <li>ResponseEntity with code 404 if the user that made the request or the provided author
     *         do not exist</li>
     *         <li>ResponseEntity with code 500 if other error occurred (e.g., server error, database error)</li>
     *     </ul>
     */
    @GetMapping("authorBooks/{authorID}")
    public ResponseEntity<Object> getBooksByAuthor(@RequestParam("userID") Long userId,
                                                   @PathVariable("authorID") Long authorId) {
        System.out.println("GET /authorBooks/{authorID} with authorID = " + authorId + " and userID = " + userId);

        if (authorId == null || userId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();
            if (user.getBanned().isBanned()) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("USER_BANNED"));
            }

            User author = userRepository.findById(authorId).orElseThrow();
            if (!author.getRole().getUserRole().equals("AUTHOR")) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("REQUESTED_USER_NOT_AN_AUTHOR"));
            }

            List<Book> books = bookService.getBooksByAuthor(author);
            List<BookDetailsResponse> response = new ArrayList<>();
            for (Book book : books) {
                response.add(new BookDetailsResponse(
                        book.getBookId(),
                        book.getTitle().getBookTitle(),
                        new AuthorsConverter().convertToDatabaseColumn(book.getAuthors()),
                        new GenresConverter().convertToDatabaseColumn(book.getGenres()),
                        new SeriesConverter().convertToDatabaseColumn(book.getSeries()),
                        book.getPageNum().getPageNum()));
            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (NoSuchElementException e) {
            System.out.println("User or author not found!");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println("Error when retrieving the books!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
