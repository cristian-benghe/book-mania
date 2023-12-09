package nl.tudelft.sem.template.example.integration.profiles;

import nl.tudelft.sem.template.example.services.BookService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockBookService")
@Configuration
public class MockBookService {

    @Bean
    @Primary
    public BookService getMockBookService() {
        return Mockito.mock(BookService.class);
    }
}
