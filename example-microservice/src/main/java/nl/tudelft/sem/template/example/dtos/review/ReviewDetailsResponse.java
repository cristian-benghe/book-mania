package nl.tudelft.sem.template.example.dtos.review;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDetailsResponse {
    private Long reviewId;
    private Long bookId;
    private Long userId;
    private List<Long> comments;
}
