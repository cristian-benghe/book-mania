package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.dtos.bookshelf.AddToBookShelfRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Builds the URL for the Bookshelf service POST.
     *
     * @param shelfId ID of shelf to which book should be added
     * @param userId ID of user who is trying to add a book
     * @return String URL for the request
     */
    public String buildBookshelfURL(long shelfId, long userId) {
        return "http://localhost:8081/bookshelf/"
            + shelfId
            + "/book" + "?userId="
            + userId
            + "&bookshelfId="
            + shelfId;
    }

    /**
     * Sends a created HTTP request to targetUrl.
     *
     * @param targetUrl URL of endpoint where to send request
     * @param requestData data to include in the request
     * @return Status code of the request
     */
    public HttpStatus checkMicroserviceStatus(
        String targetUrl,
        AddToBookShelfRequest requestData
    ) {
        // create the request that will be sent
        HttpEntity<AddToBookShelfRequest> httpRequest = new HttpEntity<>(requestData, new HttpHeaders());
        // send request, and return response
        return new RestTemplate()
            .postForEntity(targetUrl, httpRequest, String.class)
            .getStatusCode();
    }
}
