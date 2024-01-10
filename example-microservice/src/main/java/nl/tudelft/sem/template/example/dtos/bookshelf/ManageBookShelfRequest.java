package nl.tudelft.sem.template.example.dtos.bookshelf;

import lombok.Data;

@Data
public class ManageBookShelfRequest {

    private final long bookId;

    public ManageBookShelfRequest(long bookId) {
        this.bookId = bookId;
    }
}
