package nl.tudelft.sem.template.example.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class PasswordHashingService {
    /**
     * Generates a hash from a given password and salt.
     *
     * @param plaintext plaintext password
     * @param salt random salt
     * @return salted and hashed password
     */
    public String generatePasswordHash(String plaintext, String salt) {

        try {
            // hash original
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));
            String hashedPassword = new String(hashed, StandardCharsets.UTF_8);
            // add salt
            String salted = hashedPassword + salt;
            byte[] saltedHashed = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
            return new String(saltedHashed, StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException exception) {
            return plaintext;
        }
    }

    /**
     * Generates a salt for the password.
     *
     * @param length length of the salt
     * @return String salt
     */
    @SuppressWarnings("PMD") // PMD always throws an error here due to initialization of an array...
    public String generateSalt(int length) {
        char[] saltChars = new char[length];
        Random random = new Random();

        for (int i = 0; i < saltChars.length; i++) {
            int value = random.nextInt(57);
            saltChars[i] = (char) value;
        }
        return new String(saltChars);
    }
}
