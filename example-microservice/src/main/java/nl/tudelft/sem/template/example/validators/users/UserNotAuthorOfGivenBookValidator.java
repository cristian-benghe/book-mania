package nl.tudelft.sem.template.example.validators.users;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.exceptions.UserNotAuthorException;
import nl.tudelft.sem.template.example.exceptions.UserNotAuthorOfGivenBookException;
import nl.tudelft.sem.template.example.modules.user.User;

public class UserNotAuthorOfGivenBookValidator extends UserBookBaseValidator {

    @Override
    public void handle(User user) throws UserBookException {
        throw new UserBookException("Just the user was provided!");
    }

    @Override
    public void handle(User user, Book book) throws UserBookException {
        try {
            new UserNotAuthorValidator().handle(user);
            boolean isAuthor = book.getAuthors().getListAuthors().stream()
                    .anyMatch(author -> author.equals(user.getUsername().getUsername()));
            if (!isAuthor) {
                throw new UserNotAuthorOfGivenBookException(user.getUserId(), book.getBookId());
            }
        } catch (UserNotAuthorException e) {
            throw new UserNotAuthorException(user.getUserId());
        } catch (Exception e) {
            throw new UserNotAuthorOfGivenBookException(user.getUserId(), book.getBookId());
        }
        super.checkNext(user, book);
    }
}
