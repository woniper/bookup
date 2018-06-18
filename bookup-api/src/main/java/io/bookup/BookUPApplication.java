package io.bookup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Created by woniper on 2016. 2. 14..
 */
@SpringBootApplication
public class BookUPApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookUPApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}