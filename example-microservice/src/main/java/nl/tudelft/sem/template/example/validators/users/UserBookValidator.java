package nl.tudelft.sem.template.example.validators.users;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.modules.user.User;

public interface UserBookValidator {
    void setNext(UserBookValidator nextValidator);

    void handle(User user) throws UserBookException;

    void handle(User user, Book book) throws UserBookException;
}
