package nl.tudelft.sem.template.example.integration.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.controllers.UserController;
import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserResponse;
import nl.tudelft.sem.template.example.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest

public class UserControllerTest {
    UserController controller;
    @Mock
    UserService service;
    @Captor
    ArgumentCaptor<RegisterUserRequest> captor;

    @Test
    public void callsServiceAndReturns200AllCorrect() {
        // set up controller
        controller = new UserController(service);
        // use sample DTO
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
            "test@sample.com",
            "strongPassword123!",
            "my2CoolUsername1"
        );
        // mock service to return the passed data
        when(service.registerUser(any(RegisterUserRequest.class)))
            .thenAnswer(
                invocation -> new RegisterUserResponse(123L));
        // and call controller
        ResponseEntity<RegisterUserResponse> httpResponse = controller.registerNewUser(registerUserRequest);
        verify(service, times(1)).registerUser(captor.capture());
        // check if service passed correct DTO
        assertEquals(captor.getValue(), registerUserRequest);
        // check if service returned correct response
        RegisterUserResponse expected = new RegisterUserResponse(123L);
        assertEquals(httpResponse, ResponseEntity.ok(expected));
    }

    @Test
    public void callsServiceWithDisallowedUsernameAndReturns400BadRequest() {
        // set up controller
        controller = new UserController(service);
        // use sample DTO
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
            "test@sample.com",
            "strongPassword123!",
            "1WrongUsername"
        );
        // mock service to return null when username disallowed
        when(service.registerUser(registerUserRequest))
            .thenAnswer(invocation -> null);
        ResponseEntity<RegisterUserResponse> httpResponse = controller.registerNewUser(registerUserRequest);
        // check if service passed correct DTO
        verify(service, times(1)).registerUser(captor.capture());
        assertEquals(captor.getValue(), registerUserRequest);
        // check if service returned correct response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }
}
