package nl.tudelft.sem.template.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * The DTO (Data Transfer Object) used for returning the status of the user
 * after an action requested by it was forbidden.
 */
@Data
@AllArgsConstructor
public class UserStatusResponse {
    private String role;
}

