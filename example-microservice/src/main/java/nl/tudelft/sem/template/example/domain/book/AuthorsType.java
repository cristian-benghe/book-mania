package nl.tudelft.sem.template.example.domain.book;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.OneToMany;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorsType {
    @OneToMany
    private ArrayList<String> listAuthors;
}
