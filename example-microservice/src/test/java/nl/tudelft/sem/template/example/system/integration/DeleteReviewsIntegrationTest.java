package nl.tudelft.sem.template.example.system.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.controllers.collection.ModifyCollectionController;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.dtos.review.ReviewDetailsResponse;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.ModifyCollectionService;
import nl.tudelft.sem.template.example.services.RestDeleteReviewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class DeleteReviewsIntegrationTest {
    @Mock
    private ModifyCollectionService bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    private RestDeleteReviewsService restDeleteReviewsService;
    private ModifyCollectionController modifyCollectionController;

    @BeforeEach
    void setUp() {
        restDeleteReviewsService = new RestDeleteReviewsService();
        modifyCollectionController = new ModifyCollectionController(bookService,
                bookRepository,
                userRepository,
                restDeleteReviewsService);
    }

    @Test
    void deleteReviewsFromReviewMicroserviceViaBookDeletion() {
        //ignore the test if the microservice is not running
        assumeTrue(testMicroserviceConnection(), "The microservice is not running.");
        assumeTrue(restDeleteReviewsService.getReviewsFromMicroservice().isEmpty(), "The microservice db is not empty.");

        //post three reviews to their microservice
        Long userId = 1L, bookId1 = 6L, bookId2 = 7L;
        ResponseEntity<Object> responseAddReview1 = new RestTemplate().postForEntity(
                "http://localhost:8082/review/" + bookId1,
                new ReviewDetailsResponse(1L, bookId1, userId, 5.0f, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                        "2023-04-23T00:00:00Z", "2023-04-23T00:00:00Z", new ArrayList<>(), "test1"),
                Object.class);
        ResponseEntity<Object> responseAddReview2 = new RestTemplate().postForEntity(
                "http://localhost:8082/review/" + bookId2,
                new ReviewDetailsResponse(2L, bookId2, userId, 5.0f, List.of(), List.of(), List.of(),
                        "2023-04-23T00:00:00Z", "2023-04-23T00:00:00Z", List.of(), "test2"),
                Object.class);
        ResponseEntity<Object> responseAddReview3 = new RestTemplate().postForEntity(
                "http://localhost:8082/review/" + bookId2,
                new ReviewDetailsResponse(3L, bookId2, userId, 5.0f, List.of(), List.of(), List.of(),
                        "2023-04-23T00:00:00Z", "2023-04-23T00:00:00Z", List.of(), "test3"),
                Object.class);
        assertEquals(200, responseAddReview1.getStatusCodeValue());
        assertEquals(200, responseAddReview2.getStatusCodeValue());
        assertEquals(200, responseAddReview3.getStatusCodeValue());
        assertEquals(3, restDeleteReviewsService.getReviewsFromMicroservice().size());

        //delete the book2 from the collection
        User user = new User();
        user.setUserId(userId);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(bookService.deleteBook(bookId2)).thenReturn(new BookResponse(bookId2));
        modifyCollectionController.deleteBook(userId, bookId2);
        assertEquals(1, restDeleteReviewsService.getReviewsFromMicroservice().size());
    }

    private boolean testMicroserviceConnection() {
        try {
            ResponseEntity<Object> response = new RestTemplate().exchange(
                    "http://localhost:8082/getAllReviews/",
                    HttpMethod.GET,
                    null,
                    Object.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
