package nl.tudelft.sem.template.example.integration.book;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.tudelft.sem.template.example.domain.book.NumPage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * We will test this DTO mainly because
 * its constructor has conditional logic.
 */
@SpringBootTest
public class NumPageConditionalTest {
    @Test
    public void throwsErrorForNegativePages() {
        assertThrows(IllegalArgumentException.class, () -> new NumPage(-10));
    }

    @Test
    public void throwsErrorForZeroPages() {
        assertThrows(IllegalArgumentException.class, () -> new NumPage(0));
    }

    @Test
    public void allowsPositivePages() {
        assertDoesNotThrow(() -> new NumPage(1));
        assertEquals((new NumPage(1)).getPageNum(), 1);
    }
}
