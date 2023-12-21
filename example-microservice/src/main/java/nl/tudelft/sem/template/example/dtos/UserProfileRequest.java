package nl.tudelft.sem.template.example.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class UserProfileRequest {

    private final String name;

    private final String bio;

    private final String location;

    private final long favoriteBook;

    private final String profilePicture; // Encoded in base64

    private final List<String> favoriteGenres;
}
