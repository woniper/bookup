package io.bookup.book.infra.rest;

import io.bookup.book.infra.BookFinder;
import io.bookup.book.infra.rest.NaverBook.Item;
import java.util.Objects;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class NaverBookRestTemplate implements BookFinder<Item> {

    private final RestTemplate restTemplate;
    private final NaverBookProperties properties;

    public NaverBookRestTemplate(NaverBookProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Item findByIsbn(String isbn) {
        ResponseEntity<NaverBook> responseEntity = restTemplate.exchange(
                properties.createUrl(isbn),
                HttpMethod.GET,
                new HttpEntity<>(properties.getHeaderMap()),
                NaverBook.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            return responseEntity.getBody().getItems().stream()
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }
}
