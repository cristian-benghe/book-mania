package nl.tudelft.sem.template.example.config;

import java.util.List;
import nl.tudelft.sem.template.example.domain.book.Authors;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.Genre;
import nl.tudelft.sem.template.example.domain.book.Genres;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Series;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AddUsersConfig implements CommandLineRunner {
    @Autowired
    private transient UserRepository userRepository;

    @Autowired
    private transient BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {

        User u1 = new User(new UsernameType("admin"),
                new EmailType("email"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("ADMIN"),
                new DetailType(),
                new FollowingType());
        User u2 = new User(new UsernameType("user1"),
                new EmailType("email1"),
                new PasswordType("password3"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType());
        User u3 = new User(new UsernameType("user2"),
                new EmailType("email1"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType());
        userRepository.saveAll(List.of(u1, u2, u3));
        userRepository.flush();
        Book b1 = new Book(1, new Title("Nutcracker"), new Genres(List.of(Genre.ACTION)),
                new Authors(List.of("a1")), new Series(List.of("aaaa")), new NumPage(10));

        bookRepository.saveAndFlush(b1);

        User u = new User(new UsernameType("user2"),
                new EmailType("email1"),
                new PasswordType("password1"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType());
        u.getDetails().setFavouriteBookId(4);
        userRepository.saveAndFlush(u);

    }
}
