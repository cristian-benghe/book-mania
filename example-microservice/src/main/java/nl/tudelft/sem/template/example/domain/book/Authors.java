package nl.tudelft.sem.template.example.domain.book;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for the Author value object.
 */
@Data
@NoArgsConstructor
@Embeddable
public class Authors {
    private ArrayList<String> listAuthors;

    /**
     * Constructor for Authors class.
     *
     * @param authors the list of authors
     * @throws IllegalArgumentException In case the list provided is empty or null
     */
    public Authors(List<String> authors) throws IllegalArgumentException {
        if (authors != null && !authors.isEmpty()) {
            this.listAuthors = new ArrayList<>(authors);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
