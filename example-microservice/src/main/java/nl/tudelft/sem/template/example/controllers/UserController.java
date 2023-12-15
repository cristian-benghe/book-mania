package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserResponse;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.dtos.generic.InternalServerErrorResponse;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse403;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse404;
import nl.tudelft.sem.template.example.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    private final transient UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint allowing the registration of a user.
     * Passes the request body to the service running the DB functionality.
     *
     * @param userRequest DTO containing the fields of user creation request
     * @return HTTP Response with a DTO containing the fields of user creation response
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<RegisterUserResponse> registerNewUser(@RequestBody RegisterUserRequest userRequest) {
        // pass the DTO to the lower layer (services) & attempt to register user
        RegisterUserResponse userOrStatus = userService.registerUser(userRequest);
        // if registration unsuccessful, return error response
        if (userOrStatus == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // otherwise, build the result DTO and add to response
        RegisterUserResponse response = new RegisterUserResponse(userOrStatus.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint allowing a user to change their password.
     *
     * @param requestedPassword plaintext password
     * @param userId ID of user who is requesting the change
     * @return HTTP Response with optional extra information
     */
    @PutMapping("/security/password")
    @ResponseBody
    public ResponseEntity<GenericResponse> changePassword(
        @RequestBody String requestedPassword,
        @RequestParam("userID") long userId
    ) {
        // pass data to lower layer (services)
        GenericResponse response = userService.changeUserPassword(requestedPassword, userId);
        // return status
        if (response instanceof ChangePasswordResponse403) { // user forbidden
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        if (response instanceof ChangePasswordResponse404) { // user not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (response instanceof InternalServerErrorResponse) { // internal server response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

}

