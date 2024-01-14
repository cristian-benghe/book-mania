package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.dtos.UserResponse;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class VerifyUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private VerifyUserController verifyUserController;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        verifyUserController = new VerifyUserController(userService);
    }

    @Test
    void verifyUserRole1() {
        long userId = 1L;
        User user = new User();
        user.setRole(new UserEnumType("USER"));

        when(userService.getUserById(anyLong())).thenReturn(new UserResponse(user));

        ResponseEntity<String> responseEntity = verifyUserController.verifyUserRole(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("USER", responseEntity.getBody());
    }

    @Test
    void verifyUserRole2() {
        long userId = 1L;
        User user = new User();
        user.setRole(new UserEnumType("ADMIN"));

        when(userService.getUserById(anyLong())).thenReturn(new UserResponse(user));

        ResponseEntity<String> responseEntity = verifyUserController.verifyUserRole(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("ADMIN", responseEntity.getBody());
    }

    @Test
    void verifyUserRole3() {
        long userId = 1L;
        User user = new User();
        user.setRole(new UserEnumType("AUTHOR"));

        when(userService.getUserById(anyLong())).thenReturn(new UserResponse(user));

        ResponseEntity<String> responseEntity = verifyUserController.verifyUserRole(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("AUTHOR", responseEntity.getBody());
    }

    @Test
    void verifyUserRole4() {
        long userId = 1L;

        when(userService.getUserById(anyLong())).thenThrow(new RuntimeException("Some error"));

        ResponseEntity<String> responseEntity = verifyUserController.verifyUserRole(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Error retrieving user role", responseEntity.getBody());
    }
}