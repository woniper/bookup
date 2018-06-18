package io.bookup.book.infra.rest;

import io.bookup.book.domain.Book;
import io.bookup.book.domain.BookStore;
import io.bookup.book.domain.KyoboBookStore;
import io.bookup.book.domain.NaverBook;
import io.bookup.book.infra.BookFinder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
        CompletableFuture<NaverBook.Item> itemCompletableFuture =
                CompletableFuture.supplyAsync(() -> naverBookRestTemplate.findByIsbn(isbn));

        CompletableFuture<KyoboBookStore> kyoboCompletableFuture =
                CompletableFuture.supplyAsync(() -> response(isbn));

        Optional<NaverBook.Item> itemOptional = getCompletableFutureItem(itemCompletableFuture);

        if (itemOptional.isPresent()) {
            NaverBook.Item item = itemOptional.get();
            return new Book(item.getTitle(), item.getDescription(), mapToBookStore(isbn, kyoboCompletableFuture));
        }

        return null;
    }

    private KyoboBookStore response(String isbn) {
        ResponseEntity<KyoboBookStore> responseEntity =
                restTemplate.postForEntity(properties.createUrl(isbn), StringUtils.EMPTY, KyoboBookStore.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            return responseEntity.getBody();
        }

        return null;
    }

    private List<BookStore> mapToBookStore(String isbn,
                                           CompletableFuture<KyoboBookStore> kyoboBookStore) {

        if (StringUtils.isEmpty(isbn) || Objects.isNull(kyoboBookStore))
            return null;

        return getCompletableFutureItem(kyoboBookStore)
                .map(bookStore -> bookStore.getItems().stream()
                        .filter(x -> x.getAmount() > 0)
                        .map(x -> new BookStore(x.getStoreName(), properties.createUrl(x.getStoreId(), isbn)))
                        .collect(Collectors.toList())
                ).orElse(Collections.emptyList());

    }

    private <T> Optional<T> getCompletableFutureItem(CompletableFuture<T> completableFuture) {
        try {
            return Optional.ofNullable(completableFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
