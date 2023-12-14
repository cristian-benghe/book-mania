package nl.tudelft.sem.template.example.integration.shelf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.controllers.ShelfController;
import nl.tudelft.sem.template.example.dtos.bookshelf.AddToBookShelfRequest;
import nl.tudelft.sem.template.example.dtos.bookshelf.AddToBookShelfResponse;
import nl.tudelft.sem.template.example.dtos.bookshelf.AddToBookShelfResponse200;
import nl.tudelft.sem.template.example.dtos.bookshelf.AddToBookShelfResponse403;
import nl.tudelft.sem.template.example.dtos.bookshelf.AddToBookShelfResponse404;
import nl.tudelft.sem.template.example.services.RestService;
import nl.tudelft.sem.template.example.services.ShelfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class ShelfControllerTest {
    @Mock
    ShelfService shelfService;
    @Mock
    RestService restService;

    ShelfController shelfController;

    @BeforeEach
    public void setup() {
        shelfController = new ShelfController(shelfService, restService);
    }

    @Test
    public void respondsWithInternalServerErrorIfServiceEncountered500InternalServerError() {
        when(shelfService.addBookToBookshelf(123L, 2L, 5L)).thenReturn(null);
        ResponseEntity<AddToBookShelfResponse> response = shelfController.addBookToBookshelf(123L, 2L, 5L);
        assertEquals(response, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @Test
    public void respondsWith403ForbiddenIfUserBanned() {
        when(shelfService.addBookToBookshelf(123L, 2L, 5L)).thenReturn(new AddToBookShelfResponse403("USER_BANNED"));
        ResponseEntity<AddToBookShelfResponse> response = shelfController.addBookToBookshelf(123L, 2L, 5L);
        assertEquals(response, ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @Test
    public void respondsWith404NotFoundIfBookOrUserNotFound() {
        when(shelfService.addBookToBookshelf(123L, 2L, 5L)).thenReturn(new AddToBookShelfResponse404());
        ResponseEntity<AddToBookShelfResponse> response = shelfController.addBookToBookshelf(123L, 2L, 5L);
        assertEquals(response, ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Test
    public void callsExternalServiceIfDetailsCorrect() {
        // set expected response
        AddToBookShelfResponse expectedResponse = new AddToBookShelfResponse200(2L, 5L);
        String expectedUrl = "http://localhost:8081/bookshelf/2/book?userId=123&bookshelfId=2";
        // mock the services to be successful, and the other microservice to be successful as well
        when(shelfService.addBookToBookshelf(123L, 2L, 5L))
            .thenReturn(expectedResponse);
        when(restService.buildBookshelfURL(2L, 123L))
            .thenReturn(expectedUrl);
        when(restService.checkMicroserviceStatus(
            any(String.class), any(AddToBookShelfRequest.class)
        )).thenReturn(HttpStatus.OK);
        // call the endpoint
        ResponseEntity<AddToBookShelfResponse> response = shelfController
            .addBookToBookshelf(123L, 2L, 5L);
        // verify that the shelf and rest services were called with correct data
        AddToBookShelfRequest expectedData = new AddToBookShelfRequest(5L);
        verify(restService).checkMicroserviceStatus(expectedUrl, expectedData);
        verify(shelfService).addBookToBookshelf(123L, 2L, 5L);
        // and assert that correct result returned
        assertEquals(response, ResponseEntity.status(HttpStatus.OK)
            .body(expectedResponse));
    }

    @Test
    public void returns500InternalServerErrorIfServiceRespondsWithErrorCode() {
        // set expected response
        AddToBookShelfResponse expectedResponse = new AddToBookShelfResponse200(2L, 5L);
        String expectedUrl = "http://localhost:8081/bookshelf/2/book?userId=123&bookshelfId=2";
        // mock the services to be successful, and the other microservice to be failing, with one of the possible 400 codes
        when(shelfService.addBookToBookshelf(123L, 2L, 5L))
            .thenReturn(expectedResponse);
        when(restService.buildBookshelfURL(2L, 123L))
            .thenReturn(expectedUrl);
        when(restService.checkMicroserviceStatus(
            any(String.class), any(AddToBookShelfRequest.class)
        )).thenReturn(HttpStatus.UNAUTHORIZED);
        // call the endpoint
        ResponseEntity<AddToBookShelfResponse> response = shelfController
            .addBookToBookshelf(123L, 2L, 5L);
        // verify that the shelf and rest services were called with correct data
        AddToBookShelfRequest expectedData = new AddToBookShelfRequest(5L);
        verify(restService).checkMicroserviceStatus(expectedUrl, expectedData);
        verify(shelfService).addBookToBookshelf(123L, 2L, 5L);
        // and assert that INTERNAL_SERVER_ERROR returned
        assertEquals(response, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build());
    }
}
