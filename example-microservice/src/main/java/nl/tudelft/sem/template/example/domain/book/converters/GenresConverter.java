package nl.tudelft.sem.template.example.domain.book.converters;

import java.util.ArrayList;
import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.example.domain.book.Genres;

public class GenresConverter implements AttributeConverter<Genres, String> {
    /**
     * Converts a 'Genres' object into a String of genres.
     *
     * @param attribute  the entity attribute value to be converted
     * @return the String of genres to be added to the database
     */
    @Override
    public String convertToDatabaseColumn(Genres attribute) {
        if (attribute.getGenreList() == null) {
            return null;
        }

        ArrayList<String> genres = new ArrayList<>();

        for (Enum elem : attribute.getGenreList()) {
            genres.add(elem.toString());
        }

        return String.join(",", genres);
    }

    /**
     * Converts a String of genres to a 'Genres' object.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return the 'Genres' object from the database
     */
    @Override
    public Genres convertToEntityAttribute(String dbData) {
        ArrayList<Enum> genres = new ArrayList<>();

        //waiting for Review team to provide the Enums for genres. We need the class to convert strings back into enums
        //for(String elem : dbData.split(",")) {
        //    genres.add(Enum.valueOf(elem));
        //}

        return new Genres(genres);
    }
}
