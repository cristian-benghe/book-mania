package nl.tudelft.sem.template.authentication.models.book;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreType {
    private ArrayList<Enum> genreList;
}
