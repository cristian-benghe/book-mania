package nl.tudelft.sem.template.example.dtos.bookshelf;

import lombok.Data;

@Data
public class ManageBookShelfResponse403 implements ManageBookShelfResponse {
    private String role;

    public ManageBookShelfResponse403(String role) {
        this.role = role;
    }
}
