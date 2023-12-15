package nl.tudelft.sem.template.example.dtos;


import lombok.Data;

@Data
public class UserRoleResponse {
    private final String role;

    public UserRoleResponse(final String role) {
        this.role = role;
    }

    /**
     * Returns the role of the user.
     *
     * @return role of user
     */
    public String getRole() {
        return role;
    }
}
