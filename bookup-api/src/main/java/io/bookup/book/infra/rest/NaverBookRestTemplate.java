package io.bookup.book.infra.rest;

import io.bookup.book.api.Pageable;
import io.bookup.book.infra.BookFinder;
import io.bookup.book.infra.BookRepository;
import io.bookup.book.infra.rest.NaverBook.Item;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class NaverBookRestTemplate implements BookRepository<Optional<NaverBook>>, BookFinder<Optional<Item>> {

    private final RestTemplate restTemplate;
    private final NaverBookProperties properties;

    public NaverBookRestTemplate(RestTemplate restTemplate,
                                 NaverBookProperties properties) {

        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public Optional<Item> findByIsbn(String isbn) {
        Optional<NaverBook> naverBook = response(isbn);

        if (naverBook.isPresent()) {
            return naverBook
                    .get()
                    .getItems().stream()
                    .findFirst();
        }

        return Optional.empty();
    }

    @Override
    public Optional<NaverBook> findByTitle(String title, Pageable pageable) {
        return response(title, pageable);
    }

    private Optional<NaverBook> response(String query, Pageable pageable) {
        Assert.notNull(query, "not null query");

        String url;

        if (Objects.nonNull(pageable)) {
            url = properties.createUrl(query, pageable.getPage(), pageable.getSize());
        } else {
            url = properties.createUrl(query);
        }

        ResponseEntity<NaverBook> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(properties.getHeaderMap()),
                NaverBook.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            return Optional.of(responseEntity.getBody());
        }

        return Optional.empty();
    }

    private Optional<NaverBook> response(String query) {
        return response(query, null);
    }

}
