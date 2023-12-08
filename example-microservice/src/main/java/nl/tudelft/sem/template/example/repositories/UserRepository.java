package nl.tudelft.sem.template.example.repositories;

import nl.tudelft.sem.template.example.modules.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}