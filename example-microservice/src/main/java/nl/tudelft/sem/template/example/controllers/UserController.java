package nl.tudelft.sem.template.example.controllers;
import nl.tudelft.sem.template.example.dataTransferObjects.RegisterUserRequest;
import nl.tudelft.sem.template.example.dataTransferObjects.RegisterUserResponse;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<RegisterUserResponse> registerNewUser(@RequestBody RegisterUserRequest userRequest) {
        // pass the DTO to the lower layer (services) & attempt to register user
        User userOrStatus = userService.registerUser(userRequest);
        // if registration unsuccessful, return error response
        if(userOrStatus == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        // otherwise, build the result DTO and add to response
        RegisterUserResponse response = new RegisterUserResponse(userOrStatus.getUserId());
        return ResponseEntity.ok(response);
    }

}

