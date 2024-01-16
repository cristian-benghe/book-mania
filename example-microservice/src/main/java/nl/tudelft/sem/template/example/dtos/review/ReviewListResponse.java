package nl.tudelft.sem.template.example.dtos.review;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewListResponse {
    private List<ReviewDetailsResponse> reviews;
}
