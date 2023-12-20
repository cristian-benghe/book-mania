package nl.tudelft.sem.template.example.services;

import javax.transaction.Transactional;
import nl.tudelft.sem.template.example.dtos.LoginUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserResponse;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.dtos.generic.InternalServerErrorResponse;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse200;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse403;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse404;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import nl.tudelft.sem.template.example.modules.user.converters.PasswordConverter;
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

    /**
     * Service method responsible for checking if a user is logging in with correct credentials.
     *
     * @param userRequest DTO containing user's email and password
     * @return User object or `null`, depending on the status
     */
    public User loginUser(LoginUserRequest userRequest) {
        /* check if email is a valid object */
        try {
            new EmailType(userRequest.getEmail());
        } catch (IllegalArgumentException e) { // check for IllegalArgumentException, since that's what EmailType throws
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
        if (!this.passwordService.passwordEncoder()
                .matches(userRequest.getPassword(), new PasswordConverter().convertToDatabaseColumn(found.getPassword()))) {
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

    /**
     * Service method implementing the logic of changing a user's password.
     *
     * @param requestBody String equal to just the requested plaintext password
     * @param userId ID of user for whom to change the password
     * @return Status code response signifying the status of the operation
     */
    @Transactional
    public GenericResponse changeUserPassword(String requestBody, long userId) {
        try {
            // check if user exists
            if (!userRepository.existsById(userId)) { // if not, return appropriate response
                return new ChangePasswordResponse404();
            }
            // check if user banned
            if (userRepository.findById(userId).get().getBanned().isBanned()) {
                return new ChangePasswordResponse403("USER_BANNED");
            }
            // user exists & not banned -> proceed with changes
            // request body is just the new password
            User retrieved = userRepository.findById(userId).get();
            // modify password
            retrieved.setPassword(
                new PasswordType(
                    this.passwordService.passwordEncoder().encode(requestBody)
                )
            );
            // save user
            userRepository.save(retrieved);
            // and finally, return 200 status
            return new ChangePasswordResponse200();
        } catch (Exception e) { // some internal error: propagate up the layers
            return new InternalServerErrorResponse();
        }
    }
}
