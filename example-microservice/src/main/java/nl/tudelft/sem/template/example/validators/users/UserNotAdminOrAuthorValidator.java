package nl.tudelft.sem.template.example.validators.users;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.exceptions.UserNotAuthorException;
import nl.tudelft.sem.template.example.modules.user.User;

public class UserNotAdminOrAuthorValidator extends UserBookBaseValidator {
    @Override
    public void handle(User user) throws UserBookException {
        throw new UserBookException("Just the user was provided!");
    }

    @Override
    public void handle(User user, Book book) throws UserBookException {
        try {
            new UserNotAuthorOfGivenBookValidator().handle(user, book);
        } catch (UserNotAuthorException e) {
            new UserNotAdminValidator().handle(user);
        }
        super.checkNext(user, book);
    }
}
