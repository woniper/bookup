package io.bookup.book.infra.rest;

import io.bookup.book.domain.KyoboBookStore;
import io.bookup.book.infra.BookFinder;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class KyoboBookRestTemplate implements BookFinder<Optional<KyoboBookStore>> {

    private final KyoboProperties properties;
    private final RestTemplate restTemplate;

    public KyoboBookRestTemplate(KyoboProperties properties,
                                 RestTemplate restTemplate) {

        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<KyoboBookStore> findByIsbn(String isbn) {
        return response(isbn);
    }

    private Optional<KyoboBookStore> response(String isbn) {
        ResponseEntity<KyoboBookStore> responseEntity = restTemplate.postForEntity(
                properties.createUrl(isbn),
                StringUtils.EMPTY,
                KyoboBookStore.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            return Optional.of(responseEntity.getBody());
        }

        return Optional.empty();
    }
}
