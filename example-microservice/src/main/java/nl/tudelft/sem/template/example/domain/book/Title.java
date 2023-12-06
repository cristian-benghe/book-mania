package nl.tudelft.sem.template.example.domain.book;

import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for Title value object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Title {
    private String bookTitle;
}
