package nl.tudelft.sem.template.example.dtos;

public class LoginUserRequest {
    private final String email;
    private final String password;

    /**
     * Serves as the Data Transfer Object for accepting a request for a user login in.
     *
     * @param email email of the user being added
     * @param password password of the user being added
     */
    public LoginUserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Returns the email of the user creation request.
     *
     * @return String email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password of the user creation request.
     *
     * @return String password
     */
    public String getPassword() {
        return password;
    }
}
