package nl.tudelft.sem.template.example.dtos;

import lombok.Data;

@Data
public class AddToBookShelfResponse403 implements AddToBookShelfResponse {
    private String role;

    public AddToBookShelfResponse403(String role) {
        this.role = role;
    }
}
