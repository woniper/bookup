package io.bookup.book.infra.rest;

import io.bookup.book.infra.BookFinder;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class BandinLunisRestTemplate implements BookFinder<Optional<BandinLunisBook>> {

    private final String url;
    private final RestTemplate restTemplate;

    public BandinLunisRestTemplate(@Value("${bookup.crawler.bandi.url}") String url,
                                   RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<BandinLunisBook> findByIsbn(String isbn) {
        return response(isbn);
    }

    private Optional<BandinLunisBook> response(String isbn) {
        ResponseEntity<BandinLunisBook> responseEntity =
                restTemplate.getForEntity(String.format(url, isbn), BandinLunisBook.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            return Optional.of(responseEntity.getBody());
        }

        return Optional.empty();
    }
}
