package nl.tudelft.sem.template.example.integration.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.controllers.UserController;
import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserResponse;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.dtos.generic.InternalServerErrorResponse;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse200;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse403;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse404;
import nl.tudelft.sem.template.example.services.UserService;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setup() {
        // set up controller
        controller = new UserController(service);
    }

    @Test
    public void callsRegisterAndReturns200AllCorrect() {
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
    public void callsRegisterWithDisallowedUsernameAndReturns400BadRequest() {
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

    @Test
    public void callsRegisterWithEmptyPasswordAndReturns400BadRequest() {
        // use sample DTO
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
            "testingEmpty@sample.com",
            "",
            "correctUsername"
        );
        // mock service to return null when password empty (tested in User Service)
        when(service.registerUser(registerUserRequest))
            .thenAnswer(invocation -> null);
        ResponseEntity<RegisterUserResponse> httpResponse = controller.registerNewUser(registerUserRequest);
        // check if service passed correct DTO
        verify(service, times(1)).registerUser(captor.capture());
        assertEquals(captor.getValue(), registerUserRequest);
        // check if service returned correct response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Test
    public void callsRegisterWithEmptyEmailAndReturns400BadRequest() {
        // use sample DTO
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
            "",
            "strongPassword123!",
            "correctUsername"
        );
        // mock service to return null when email empty (tested in User Service)
        when(service.registerUser(registerUserRequest))
            .thenAnswer(invocation -> null);
        ResponseEntity<RegisterUserResponse> httpResponse = controller.registerNewUser(registerUserRequest);
        // check if service passed correct DTO
        verify(service, times(1)).registerUser(captor.capture());
        assertEquals(captor.getValue(), registerUserRequest);
        // check if service returned correct response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Test
    public void callsChangePasswordAndReturns200Ok() {
        // mock the service to accept request
        when(service.changeUserPassword(any(String.class), any(Long.class))).thenReturn(new ChangePasswordResponse200());
        // call the endpoint method
        ResponseEntity<GenericResponse> httpResponse = controller.changePassword("newPassword", 123L);
        // verify that returns correct HTTP response
        assertEquals(httpResponse, ResponseEntity.ok().build());
    }

    @Test
    public void callsChangePasswordAndReturns404ForMissingUser() {
        // mock the service to accept request
        when(service.changeUserPassword(any(String.class), any(Long.class))).thenReturn(new ChangePasswordResponse404());
        // call the endpoint method
        ResponseEntity<GenericResponse> httpResponse = controller.changePassword("newPassword", 123L);
        // verify that returns correct HTTP response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Test
    public void callsChangePasswordAndReturns403ForBannedUser() {
        // response that will be returned by the service
        GenericResponse service403 = new ChangePasswordResponse403("USER_BANNED");
        // mock the service to accept request
        when(service.changeUserPassword(any(String.class), any(Long.class))).thenReturn(service403);
        // call the endpoint method
        ResponseEntity<GenericResponse> httpResponse = controller.changePassword("newPassword", 123L);
        // verify that returns correct HTTP response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.FORBIDDEN).body(service403));
    }

    @Test
    public void callsChangePasswordAndReturns500ForInternalError() {
        // mock the service to accept request
        when(service.changeUserPassword(any(String.class), any(Long.class))).thenReturn(new InternalServerErrorResponse());
        // call the endpoint method
        ResponseEntity<GenericResponse> httpResponse = controller.changePassword("newPassword", 123L);
        // verify that returns correct HTTP response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
