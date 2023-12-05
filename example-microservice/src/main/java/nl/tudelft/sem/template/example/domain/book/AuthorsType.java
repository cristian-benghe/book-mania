package nl.tudelft.sem.template.example.domain.book;

import java.util.ArrayList;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorsType {
    @OneToMany
    private ArrayList<String> listAuthors;
}
