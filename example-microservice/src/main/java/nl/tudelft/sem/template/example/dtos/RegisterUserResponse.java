package nl.tudelft.sem.template.example.dtos;

import lombok.Data;

@Data
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

