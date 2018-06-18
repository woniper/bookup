package io.bookup.book.app;

import io.bookup.book.domain.Book;
import io.bookup.book.domain.BookStore;
import io.bookup.book.domain.KyoboBookStore;
import io.bookup.book.infra.crawler.AladinBookCrawler;
import io.bookup.book.infra.rest.KyoboBookRestTemplate;
import io.bookup.book.infra.rest.KyoboProperties;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author woniper
 */
@Service
public class BookStoreCompositeAppService {

    private final AladinBookCrawler aladinBookCrawler;
    private final KyoboBookRestTemplate kyoboBookRestTemplate;
    private final KyoboProperties kyoboProperties;

    public BookStoreCompositeAppService(AladinBookCrawler aladinBookCrawler,
                                        KyoboBookRestTemplate kyoboBookRestTemplate,
                                        KyoboProperties kyoboProperties) {

        this.aladinBookCrawler = aladinBookCrawler;
        this.kyoboBookRestTemplate = kyoboBookRestTemplate;
        this.kyoboProperties = kyoboProperties;
    }

    public Book getBook(String isbn) {
        Book aladinBook = aladinBookCrawler.findByIsbn(isbn);
        List<BookStore> bookStores = mapToBookStore(isbn, kyoboBookRestTemplate.findByIsbn(isbn));
        aladinBook.merge(bookStores);

        return aladinBook;
    }

    private List<BookStore> mapToBookStore(String isbn, Optional<KyoboBookStore> kyoboBookStore) {
        if (StringUtils.isEmpty(isbn) || !kyoboBookStore.isPresent())
            return null;

        return kyoboBookStore.get().getItems().stream()
                .filter(x -> x.getAmount() > 0)
                .map(x -> new BookStore(x.getStoreName(), kyoboProperties.createUrl(x.getStoreId(), isbn)))
                .collect(Collectors.toList());

    }
}
