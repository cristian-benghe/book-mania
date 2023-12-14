package nl.tudelft.sem.template.example.dtos.bookshelf;

import lombok.Data;

@Data
public class AddToBookShelfRequest {

    private final long bookId;

    public AddToBookShelfRequest(long bookId) {
        this.bookId = bookId;
    }
}
