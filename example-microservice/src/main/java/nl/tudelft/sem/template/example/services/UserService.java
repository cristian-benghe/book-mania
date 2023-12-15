package nl.tudelft.sem.template.example.services;

import javax.transaction.Transactional;

import nl.tudelft.sem.template.example.dtos.LoginUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserResponse;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final transient PasswordService passwordService;
    private final transient UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public User loginUser(LoginUserRequest userRequest) {
        /* check if email is a valid object */
        try {
            new EmailType(userRequest.getEmail());
        } catch (Exception e) {
            // email cannot be used or entity threw an exception during creation
            return null;
        }
        // and if so, create.
        EmailType emailT = new EmailType(userRequest.getEmail());
        // now, check if User with this email already in DB
        User found = this.userRepository.findUserByEmail(emailT);
        if (found == null) { // if user not found in DB by email
            return null;
        }

        /* check if password matches */

        if (!this.passwordService.passwordEncoder().matches(userRequest.getPassword(), found.getPassword().getPassword())) { // if password does not match with the one in DB
            return null;
        }

        return found;
    }

    /**
     * Service method implementing the username & email checks and saving to DB.
     *
     * @param userRequest DTO of the user request body
     * @return User object or `null`, depending on the status
     */
    @Transactional
    public RegisterUserResponse registerUser(RegisterUserRequest userRequest) {
        // check if email is a valid object
        try {
            new EmailType(userRequest.getEmail());
        } catch (Exception e) {
            // email cannot be used or entity threw an exception during creation
            return null;
        }
        // and if so, create.
        EmailType emailT = new EmailType(userRequest.getEmail());
        // now, check if User with this email already in DB
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

        // check if password non-empty
        if (userRequest.getPassword().isBlank()) {
            return null;
        }

        String passwordHashed = this.passwordService.passwordEncoder().encode(userRequest.getPassword());
        try {
            // try instantiating the User
            User user = new User(
                new UsernameType(userRequest.getUsername()),
                new EmailType(userRequest.getEmail()),
                new PasswordType(passwordHashed),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType()   // no followers
            );

            userRepository.save(user);
            return new RegisterUserResponse(user.getUserId());

        } catch (Exception e) {
            // Entity threw an exception during creation
            return null;
        }

    }
}
