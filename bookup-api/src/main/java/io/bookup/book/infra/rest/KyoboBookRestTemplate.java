package io.bookup.book.infra.rest;

import io.bookup.book.domain.Book;
import io.bookup.book.domain.KyoboBookStore;
import io.bookup.book.domain.NaverBook;
import io.bookup.book.infra.BookFinder;
import io.bookup.book.domain.BookStore;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class KyoboBookRestTemplate implements BookFinder<Book> {

    private final NaverBookRestTemplate naverBookRestTemplate;
    private final KyoboProperties properties;
    private final RestTemplate restTemplate;

    public KyoboBookRestTemplate(NaverBookRestTemplate naverBookRestTemplate,
                                 KyoboProperties properties,
                                 RestTemplate restTemplate) {

        this.naverBookRestTemplate = naverBookRestTemplate;
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public Book findByIsbn(String isbn) {
        NaverBook.Item item = naverBookRestTemplate.findByIsbn(isbn);
        KyoboBookStore kyoboBookStore = response(isbn);

        return new Book(item.getTitle(), item.getDescription(), mapToBookStore(isbn, kyoboBookStore));
    }

    private KyoboBookStore response(String isbn) {
        ResponseEntity<KyoboBookStore> responseEntity =
                restTemplate.postForEntity(properties.createUrl(isbn), null, KyoboBookStore.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            return responseEntity.getBody();
        }

        return null;
    }

    private List<BookStore> mapToBookStore(String isbn, KyoboBookStore kyoboBookStore) {
        if (Objects.isNull(kyoboBookStore) || CollectionUtils.isEmpty(kyoboBookStore.getItems()))
            return null;

        return kyoboBookStore.getItems().stream()
                .filter(x -> x.getAmount() > 0)
                .map(x -> new BookStore(x.getStoreName(), properties.createUrl(x.getStoreId(), isbn)))
                .collect(Collectors.toList());
    }
}
