package nl.tudelft.sem.template.example.repositories;

import nl.tudelft.sem.template.example.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
