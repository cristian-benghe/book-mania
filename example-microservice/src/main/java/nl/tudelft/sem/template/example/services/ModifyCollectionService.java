package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.dtos.book.BookRequest;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class ModifyCollectionService {
    private final transient BookRepository bookRepository;

    /**
     * Constructor for the BookService.
     *
     * @param bookRepository repository used by the service
     */
    public ModifyCollectionService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Adds a book to the database.
     *
     * @param requestBody Json of the book to be added to the database
     * @return
     *     <ul>
     *         <li>BookResponse with the ID of the book if it was added successfully</li>
     *         <li>throws IllegalArgumentException if the given book request or creator ID are null</li>
     *     </ul>
     */
    public BookResponse addBook(Long creatorId, BookRequest requestBody) throws IllegalArgumentException {
        if (requestBody == null || creatorId == null) {
            throw new IllegalArgumentException();
        }

        Book book = new Book(
                creatorId,
                new Title(requestBody.getTitle()),
                new GenresConverter().convertToEntityAttribute(requestBody.getGenre()),
                new AuthorsConverter().convertToEntityAttribute(requestBody.getAuthor()),
                new SeriesConverter().convertToEntityAttribute(requestBody.getSeries()),
                new NumPage(requestBody.getNumberOfPages()));

        Book savedBook = bookRepository.save(book);
        return new BookResponse(savedBook.getBookId());
    }
}
