package nl.tudelft.sem.template.authentication.models.Book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreType {
    private ArrayList<Enum> genreList;
}
