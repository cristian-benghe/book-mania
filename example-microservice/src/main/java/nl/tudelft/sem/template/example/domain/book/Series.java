package nl.tudelft.sem.template.example.domain.book;

import java.util.ArrayList;
import javax.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for Series value object.
 */
@Data
@NoArgsConstructor
@Embeddable
public class Series {
    private ArrayList<String> listSeries;

    /**
     * Constructor for the Series class.
     *
     * @param listSeries A list of series
     * @throws IllegalArgumentException In case the list is empty or null
     */
    public Series(ArrayList<String> listSeries) throws IllegalArgumentException {
        if (listSeries != null && !listSeries.isEmpty()) {
            this.listSeries = listSeries;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
