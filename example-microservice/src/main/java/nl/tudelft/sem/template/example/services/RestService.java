package nl.tudelft.sem.template.example.services;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
