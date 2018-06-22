package io.bookup.book.infra.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bookup.book.api.Pageable;
import io.bookup.book.infra.BookFinder;
import io.bookup.book.infra.BookRepository;
import io.bookup.book.infra.rest.NaverBook.Item;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
@Slf4j
@Component
public class NaverBookRestTemplate implements BookRepository<Optional<NaverBook>>, BookFinder<Optional<Item>> {

    private final RestTemplate restTemplate;
    private final NaverBookProperties properties;
    private final ObjectMapper objectMapper;

    public NaverBookRestTemplate(RestTemplate restTemplate,
                                 NaverBookProperties properties,
                                 ObjectMapper objectMapper) {

        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
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

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(properties.getHeaderMap()),
                String.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            try {
                String body = responseEntity
                        .getBody()
                        .replaceAll("<b>", StringUtils.EMPTY)
                        .replaceAll("</b>", StringUtils.EMPTY);

                return Optional.of(objectMapper.readValue(body, NaverBook.class));
            } catch (IOException e) {
                log.error("naver book mapping error", e);
            }
        }

        return Optional.empty();
    }

    private Optional<NaverBook> response(String query) {
        return response(query, null);
    }

}
