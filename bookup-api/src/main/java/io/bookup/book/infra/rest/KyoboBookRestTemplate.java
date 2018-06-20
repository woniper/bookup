package io.bookup.book.infra.rest;

import io.bookup.book.domain.BookStore;
import io.bookup.book.infra.BookFinder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class KyoboBookRestTemplate implements BookFinder<List<BookStore>> {

    private final KyoboProperties properties;
    private final RestTemplate restTemplate;

    public KyoboBookRestTemplate(KyoboProperties properties,
                                 RestTemplate restTemplate) {

        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<BookStore> findByIsbn(String isbn) {
        KyoboBookStore kyoboBookStore = response(isbn);
        return mapToBookStore(isbn, kyoboBookStore);
    }

    private KyoboBookStore response(String isbn) {
        ResponseEntity<KyoboBookStore> responseEntity = restTemplate.postForEntity(
                properties.createUrl(isbn),
                StringUtils.EMPTY,
                KyoboBookStore.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            return responseEntity.getBody();
        }

        return null;
    }

    private List<BookStore> mapToBookStore(String isbn, KyoboBookStore kyoboBookStore) {
        if (StringUtils.isEmpty(isbn) || Objects.isNull(kyoboBookStore)) return Collections.emptyList();

        return kyoboBookStore.getItems().stream()
                .filter(x -> x.getAmount() > 0)
                .map(x -> new BookStore(x.getStoreName(), properties.createUrl(x.getStoreId(), isbn)))
                .collect(Collectors.toList());

    }
}
