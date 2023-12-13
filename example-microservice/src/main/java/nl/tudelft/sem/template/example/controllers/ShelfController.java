package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.dtos.AddToBookShelfRequest;
import nl.tudelft.sem.template.example.dtos.AddToBookShelfResponse;
import nl.tudelft.sem.template.example.dtos.AddToBookShelfResponse200;
import nl.tudelft.sem.template.example.dtos.AddToBookShelfResponse403;
import nl.tudelft.sem.template.example.dtos.AddToBookShelfResponse404;
import nl.tudelft.sem.template.example.services.RestService;
import nl.tudelft.sem.template.example.services.ShelfService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShelfController {

    private final transient ShelfService shelfService;
    private final transient RestService restService;

    public ShelfController(ShelfService shelfService, RestService restService) {
        this.shelfService = shelfService;
        this.restService = restService;
    }

    /**
     * Endpoint that allows users to add books to their collections.
     * Validates a request and passes it to the BookService API s.t. the relation can be stored.
     *
     * @param userId ID of user for whom the book is to be added
     * @param shelfId ID of shelf to which the book should be added
     * @param bookId ID of book that should be added
     * @return Response (200, 403, 404 xor 500) depending on status
     */
    @PostMapping("/shelf")
    @ResponseBody
    public ResponseEntity<AddToBookShelfResponse> addBookToBookshelf(
        @RequestParam("userID") long userId,
        @RequestParam("shelfID") long shelfId,
        @RequestParam("bookID") long bookId
    ) {
        // pass the parameters to the lower layer (service)
        AddToBookShelfResponse
            detailsOrStatus = shelfService.addBookToBookshelf(userId, shelfId, bookId);
        // check if server error thrown
        if (detailsOrStatus == null) {
            // server error encountered
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // check if user banned/disallowed
        if (detailsOrStatus instanceof AddToBookShelfResponse403) {
            // user not allowed
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // check if book/user DNE
        if (detailsOrStatus instanceof AddToBookShelfResponse404) {
            // book or user do not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // check if validated by user
        if (detailsOrStatus instanceof AddToBookShelfResponse200) {
            // call the other microservice's endpoint
            AddToBookShelfRequest requestData =
                new AddToBookShelfRequest(((AddToBookShelfResponse200) detailsOrStatus).getBookID());
            HttpEntity<AddToBookShelfRequest> addToBookshelf = new HttpEntity<>(requestData, new HttpHeaders());
            // send request and get response
            String targetUrl =
                "http://localhost:8081/bookshelf/"
                    + (requestData.getBookId())
                    + "/book" + "?userId="
                    + userId
                    + "&bookshelfId="
                    + shelfId;
            ResponseEntity<Object> response =
                restService.restTemplate().postForEntity(targetUrl, addToBookshelf, Object.class);
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                // status code signifies failure
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            // else, return correct entity details
            return ResponseEntity.status(HttpStatus.OK).body(detailsOrStatus);
        }
        // final return statement: if all fails, INTERNAL_SERVER_ERROR
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
