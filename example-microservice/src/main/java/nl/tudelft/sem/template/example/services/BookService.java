package nl.tudelft.sem.template.example.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.NumPageConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.domain.book.converters.TitleConverter;
import nl.tudelft.sem.template.example.dtos.book.BookContentResponse;
import nl.tudelft.sem.template.example.dtos.book.BookListResponse;
import nl.tudelft.sem.template.example.dtos.book.BookRequest;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Updates a book from the database. From the provided fields in the body,
     * only the ones that are not null and have a valid format will be updated.
     *
     * @param bookId the id of the book to be updated
     * @return
     *     <ul>
     *         <li>BookResponse with the ID of the updated book if the update was successful</li>
     *         <li>BookResponse with a null ID if the book does not exist</li>
     *         <li>null if the book was not updated successfully (e.g. invalid id, database error)</li>
     *     </ul>
     */
    @Transactional
    public BookResponse updateBook(Long bookId, BookRequest newBook) {
        try {
            Book book = bookRepository.findById(bookId).orElseThrow();
            try {
                book.setAuthors(new AuthorsConverter().convertToEntityAttribute(newBook.getAuthor()));
            } catch (Exception e) {
                System.out.println("Invalid author when updating book!");
            }
            try {
                book.setGenres(new GenresConverter().convertToEntityAttribute(newBook.getGenre()));
            } catch (Exception e) {
                System.out.println("Invalid genre when updating book!");
            }
            try {
                book.setPageNum(new NumPageConverter().convertToEntityAttribute(newBook.getNumberOfPages()));
            } catch (Exception e) {
                System.out.println("Invalid number of pages when updating book!");
            }
            try {
                book.setTitle(new TitleConverter().convertToEntityAttribute(newBook.getTitle()));
            } catch (Exception e) {
                System.out.println("Invalid title when updating book!");
            }
            try {
                book.setSeries(new SeriesConverter().convertToEntityAttribute(newBook.getSeries()));
            } catch (Exception e) {
                System.out.println("Invalid series when updating book!");
            }
            bookRepository.save(book);
            return new BookResponse(bookId);
        } catch (NoSuchElementException e) {
            return new BookResponse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Deletes a book from the database.
     *
     * @param bookId the id of the book to be deleted
     * @return
     *     <ul>
     *         <li>BookResponse with the ID of the deleted book if the deletion was successful</li>
     *         <li>BookResponse with a null ID if the book does not exist</li>
     *         <li>null if the book was not deleted successfully (e.g. invalid id, database error)</li>
     *     </ul>
     */
    public BookResponse deleteBook(Long bookId) {
        try {
            if (bookRepository.findById(bookId).isEmpty()) {
                return new BookResponse(null);
            }
            bookRepository.deleteById(bookId);
            return new BookResponse(bookId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns a list of books that have the given author.
     *
     * @param author the author of the books to be returned
     * @return a list of books that have the given author
     */
    public BookListResponse getBooksByAuthor(User author) {
        List<Book> books = bookRepository.findAll();
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthors().getListAuthors().contains(author.getDetails().getName())) {
                result.add(book);
            }
        }
        return new BookListResponse(result);
    }
}
