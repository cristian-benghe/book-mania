package nl.tudelft.sem.template.example.dataTransferObjects;

public class RegisterUserResponse {
    private final long userId;

    public RegisterUserResponse(long userId) {
        this.userId = userId;
    }

    /**
     * Returns the ID of created user.
     *
     * @return ID of user
     */
    public long getUserId() {
        return userId;
    }
}

