package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserResponse;
import nl.tudelft.sem.template.example.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * @return DTO containing the fields of user creation response
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

}

