package nl.tudelft.sem.template.example.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailResponse {
    private final String name;

    private final String bio;

    private final String location;

    private final long favoriteBook;

    private final String profilePicture;

    private final List<String> favoriteGenres;
}
