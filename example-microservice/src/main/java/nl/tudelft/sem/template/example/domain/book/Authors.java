package nl.tudelft.sem.template.example.domain.book;

import java.util.ArrayList;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for the Author value object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Authors {
    private ArrayList<String> listAuthors;
}
