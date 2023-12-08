package nl.tudelft.sem.template.example.controllers;
import nl.tudelft.sem.template.example.dataTransferObjects.RegisterUserRequest;
import nl.tudelft.sem.template.example.dataTransferObjects.RegisterUserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    // save DB here

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<RegisterUserResponse> registerNewUser(@RequestBody RegisterUserRequest userRequest) {

        return null;
    }

}

