package nl.tudelft.sem.template.example.domain.book;

import java.util.ArrayList;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for Genre value object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Genres {
    private ArrayList<Enum> genreList;
}
