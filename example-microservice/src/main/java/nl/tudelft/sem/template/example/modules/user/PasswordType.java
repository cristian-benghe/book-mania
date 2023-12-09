package nl.tudelft.sem.template.example.modules.user;

import javax.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
public class PasswordType {
    private String password;
    private String salt;

    /**
     * Constructor for the PasswordType value object.
     *
     * @param password password
     * @throws IllegalArgumentException if the password is invalid
     */
    public PasswordType(String password, String salt) throws IllegalArgumentException {
        if (!password.isEmpty()) {
            this.password = password;
            this.salt = salt;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
