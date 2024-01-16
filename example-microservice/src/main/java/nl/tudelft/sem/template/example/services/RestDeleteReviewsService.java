package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.dtos.review.ReviewListResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestDeleteReviewsService {

    private final transient String microServiceURL = "http://localhost:8082/";

    /**
     * Builds the URL for the getting the reviews for a book.
     *
     * @param bookId ID of the book
     * @return The URL for the request of getting all the reviews for a provided book
     */
    public String buildGetReviewsURL(Long bookId) {
        return microServiceURL + "getAllReviews/" + bookId;
    }

    /**
     * Builds the URL for the deleting a review.
     *
     * @param reviewId ID of the review
     * @param userId ID of the user who wrote the review
     * @return The URL for the request of deleting a review
     */
    public String buildDeleteReviewURL(Long reviewId, Long userId) {
        return microServiceURL + "review/delete/" + reviewId + "/" + userId;
    }

    /**
     * Sends a created HTTP request to targetUrl.
     *
     * @param bookId ID of the book containing the reviews
     * @return The list of reviews for the book
     */
    public ReviewListResponse getReviewsFromMicroservice(Long bookId) {
        return new RestTemplate()
                .getForEntity(buildGetReviewsURL(bookId), ReviewListResponse.class)
                .getBody();
    }

    /**
     * Sends a DELETE HTTP to delete a review.
     *
     * @param reviewId ID of the review
     * @param userId ID of the user who wrote the review
     * @return Status code of the request
     */
    public HttpStatus deleteReviewFromMicroservice(Long reviewId, Long userId) {
        return new RestTemplate()
                .exchange(buildDeleteReviewURL(reviewId, userId),
                        HttpMethod.DELETE,
                        null,
                        String.class)
                .getStatusCode();
    }
}
