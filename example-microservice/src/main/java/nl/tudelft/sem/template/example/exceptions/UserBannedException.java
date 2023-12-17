package nl.tudelft.sem.template.example.exceptions;

public class UserBannedException extends Exception {
    public UserBannedException() {
        super("User is banned");
    }

    public UserBannedException(String message) {
        super(message);
    }

    public UserBannedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserBannedException(Long userId) {
        super("User banned with ID: " + userId);
    }

    public UserBannedException(Throwable cause) {
        super(cause);
    }
}
