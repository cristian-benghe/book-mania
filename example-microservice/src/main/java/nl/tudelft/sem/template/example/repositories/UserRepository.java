package nl.tudelft.sem.template.example.repositories;

import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Definition of a query allowing finding a user by their email.
     *
     * @param email EmailType object of the queried user
     * @return User entity if found, null otherwise
     */
    public User findUserByEmail(EmailType email);
}
