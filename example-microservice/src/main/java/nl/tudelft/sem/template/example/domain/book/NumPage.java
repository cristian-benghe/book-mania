package nl.tudelft.sem.template.example.domain.book;

import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for NumPage (number of pages) value object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class NumPage {
    private int numPages;
}
