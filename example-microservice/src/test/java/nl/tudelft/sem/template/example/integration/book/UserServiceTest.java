package nl.tudelft.sem.template.example.integration.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import nl.tudelft.sem.template.example.services.PasswordHashingService;
import nl.tudelft.sem.template.example.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mockUserRepository")
@AutoConfigureMockMvc
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    UserService service;
    @Mock
    PasswordHashingService passwordHashingService;
    @Captor
    ArgumentCaptor<User> userCaptor;

    @Test
    public void registerUserTestCorrectUnameCorrectEmailCheckDb() {
        // set up mock DB
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        // and set up mock password hashing service
        when(passwordHashingService.generatePasswordHash(any(String.class), any(String.class)))
            .thenReturn("0xHashedPasswordx0");
        when(passwordHashingService.generateSalt(any(Integer.class)))
            .thenReturn("_passwordSalt_");
        // set up capture
        service = new UserService(userRepository, passwordHashingService);

        // provide sample DTO
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "unhashedPW", "correctUname1");

        // call the registration method
        service.registerUser(registrationReq);
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();

        // expected User
        User expected = new User(
            new UsernameType("correctUname1"),
            new EmailType("example@foo.com"),
            new PasswordType("0xHashedPasswordx0", "_passwordSalt_"),
            new BannedType(false),
            new PrivacyType(false),
            new UserEnumType("USER"),
            new DetailType(),
            new FollowingType()   // no followers
        );

        assertEquals(expected, captured);
    }

    @Test
    public void registerUserTestCorrectUnameCorrectEmailCheckResponseOneUser() {
        // set up mock DB
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        // and set up mock password hashing service
        when(passwordHashingService.generatePasswordHash(any(String.class), any(String.class)))
            .thenReturn("0xHashedPasswordx0");
        when(passwordHashingService.generateSalt(any(Integer.class)))
            .thenReturn("_passwordSalt_");
        // provide sample DTO
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "unhashedPW", "correctUname1");
        // set up service
        service = new UserService(userRepository, passwordHashingService);
        // call the registration method
        RegisterUserResponse response = service.registerUser(registrationReq);
        assertEquals(response, new RegisterUserResponse(0));
    }

    @Test
    public void registerUserTestCorrectUnameCorrectEmailCheckResponseMultipleUsers() {
        // set up mock DB
        // assume that new user is _assigned ID>0_
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> (mockHelper((User) invocation.getArgument(0), 123L)));
        // and set up mock password hashing service
        when(passwordHashingService.generatePasswordHash(any(String.class), any(String.class)))
            .thenReturn("0xHashedPasswordx0");
        when(passwordHashingService.generateSalt(any(Integer.class)))
            .thenReturn("_passwordSalt_");
        // provide sample DTO
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "unhashedPW", "correctUname1");
        service = new UserService(userRepository, passwordHashingService);
        // check if response is correct
        RegisterUserResponse response = service.registerUser(registrationReq);
        assertEquals(response, new RegisterUserResponse(123));
    }

    /**
     * Takes an existing user and modifies their ID, for testing purposes only.
     *
     * @param user original user
     * @param testId ID to be set
     * @return this user but with new ID
     */
    private User mockHelper(User user, long testId) {
        user.setUserId(testId);
        return user;
    }

}
