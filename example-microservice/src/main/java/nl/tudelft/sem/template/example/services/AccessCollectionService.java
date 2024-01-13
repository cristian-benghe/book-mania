package nl.tudelft.sem.template.example.services;

import java.util.ArrayList;
import nl.tudelft.sem.template.example.dtos.book.BookContentResponse;
import nl.tudelft.sem.template.example.dtos.book.BookListResponse;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class AccessCollectionService {

    private final transient BookRepository bookRepository;

    /**
     * Constructor for the AccessCollectionService.
     *
     * @param bookRepository repository used by the service
     */
    public AccessCollectionService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Retrieves a book from the database by its ID.
     *
     * @param bookId The ID of the book to be returned
     * @return
     *     <ul>
     *         <li>BookContentResponse with the contents of the book with the given ID if it exists in the database</li>
     *         <li>Throws NoSuchElementException if the given bookID is not found in the database</li>
     *     </ul>
     */
    public BookContentResponse getBook(Long bookId) {
        return new BookContentResponse(bookRepository.findById(bookId).orElseThrow());
    }

    /**
     * Retrieves a list of all distinct books from the database.
     * The method calls bookRepository.findAll() which returns a List.
     * Then, it uses the returned List to create a Set to automatically eliminate duplicate books.
     *
     * @return
     *     <ul>
     *         <li>BookListResponse with a list of all distinct books in the database</li>
     *     </ul>
     */
    public BookListResponse getAllBooks() {
        return new BookListResponse(new ArrayList<>(bookRepository.findAll()));
    }
}
