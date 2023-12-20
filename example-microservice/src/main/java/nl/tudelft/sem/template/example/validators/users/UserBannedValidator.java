package nl.tudelft.sem.template.example.validators.users;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.modules.user.User;

public class UserBannedValidator extends UserBookBaseValidator {
    @Override
    public void handle(User user) throws UserBookException {
        if (user.getBanned().isBanned()) {
            throw new UserBannedException(user.getUserId());
        }
    }

    @Override
    public void handle(User user, Book book) throws UserBookException {
        handle(user);
        super.checkNext(user, book);
    }
}
