package nl.tudelft.sem.template.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
public class UserProfileRequest {

    @Getter
    private final String name;

    @Getter
    private final String bio;

    @Getter
    private final String location;

    @Getter
    private final long favoriteBook;

    @Getter
    private final String profilePicture; // Encoded in base64

    @Getter
    private final List<String> favoriteGenres;
}
