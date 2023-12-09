package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserResponse;
import nl.tudelft.sem.template.example.modules.user.*;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final transient UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Service method implementing the username & email checks and saving to DB.
     *
     * @param userRequest DTO of the user request body
     * @return User object or `null`, depending on the status
     */
    public RegisterUserResponse registerUser(RegisterUserRequest userRequest) {
        // check if User with this email already in DB
        EmailType emailT = new EmailType(userRequest.getEmail());
        User found = this.userRepository.findUserByEmail(emailT);
        if (found != null) { // user with this email already exists
            return null;
        }

        // check if username follows convention
        String username = userRequest.getUsername();
        boolean matchConvention =
            username.matches("^[^0-9][a-zA-Z0-9]*");
        if (!matchConvention) {
            return null;
        }

        // username checks out & email not present in DB -> register user
        User user = new User(
            new UsernameType(userRequest.getUsername()),
            new EmailType(userRequest.getEmail()),
            new PasswordType(userRequest.getPassword()), // TODO: swap this for hashed pw
            new BannedType(false),
            new PrivacyType(false),
            new UserEnumType("USER"),
            new DetailType(),
            new FollowingType()   // no followers
        );

        userRepository.save(user);
        return new RegisterUserResponse(user.getUserId());

    }
}
