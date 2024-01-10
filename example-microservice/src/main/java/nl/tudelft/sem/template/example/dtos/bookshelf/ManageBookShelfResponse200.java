package nl.tudelft.sem.template.example.dtos.bookshelf;

import lombok.Data;

@Data
public class ManageBookShelfResponse200 implements AddToBookShelfResponse {

    @SuppressWarnings("")
    private long shelfID;
    @SuppressWarnings("")
    private long bookID;

    public ManageBookShelfResponse200(long shelfID, long bookID) {
        this.shelfID = shelfID;
        this.bookID = bookID;
    }
}
