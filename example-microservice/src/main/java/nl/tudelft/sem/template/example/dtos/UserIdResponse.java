package nl.tudelft.sem.template.example.dtos;

import lombok.Data;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;

@Data
public class UserIdResponse implements GenericResponse {
    private final long userId;

    public UserIdResponse(long userId) {
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

