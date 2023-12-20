package nl.tudelft.sem.template.example.validators.users;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.modules.user.User;

public abstract class UserBookBaseValidator implements UserBookValidator {
    private transient UserBookValidator nextValidator;

    @Override
    public void setNext(UserBookValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    protected void checkNext(User user, Book book) throws UserBookException {
        if (nextValidator != null) {
            nextValidator.handle(user, book);
        }
    }
}
