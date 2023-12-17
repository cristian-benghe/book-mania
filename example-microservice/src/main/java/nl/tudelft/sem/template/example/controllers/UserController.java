package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.dtos.LoginUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserResponse;
import nl.tudelft.sem.template.example.dtos.UserRoleResponse;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
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

    /**
     * Endpoint allowing the login of a user.
     *
     * @param userRequest DTO containing the fields of user login request
     * @return Appropriate response entity
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> loginUser(@RequestBody LoginUserRequest userRequest) {
        try {
            // check if user can successfully login
            final User user = userService.loginUser(userRequest);

            // if user is not found, either due to no user existing or wrong password or email, return 401 unauthorized
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password and/or email address");
            }

            // if user is banned return 403 forbidden
            if (new BannedConverter().convertToDatabaseColumn(user.getBanned())) {
                final UserRoleResponse role = new UserRoleResponse("USER_BANNED");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(role);
            }

            RegisterUserResponse response = new RegisterUserResponse(user.getUserId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            // Step 4: Handle internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

