package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final transient BookRepository bookRepository;

    /**
     * Constructor for the BookService.
     *
     * @param bookRepository repository used by the service
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Adds a book to the database.
     *
     * @param book book to be added to the database
     * @return book that was added to the database if successful, null otherwise
     */
    public Book insert(Book book) {
        if (book == null) {
            throw new IllegalArgumentException();
        }
        return bookRepository.save(book);
    }
}
