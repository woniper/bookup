package io.bookup.book.app;

import io.bookup.book.domain.BookStore;
import io.bookup.book.domain.NotFoundBookException;
import io.bookup.book.domain.Store;
import io.bookup.book.infra.BookFinder;
import io.bookup.book.infra.crawler.AladinBookCrawler;
import io.bookup.book.infra.crawler.BandinLunisBookCrawler;
import io.bookup.book.infra.rest.KyoboBookRestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import static io.bookup.common.utils.FutureUtils.getFutureItem;

/**
 * @author woniper
 */
@Service
public class BookStoreCompositeAppService {

    private final BookFindAppService bookFindAppService;
    private final List<BookFinder<List<Store>>> bookStoreFinders;

    public BookStoreCompositeAppService(BookFindAppService bookFindAppService,
                                        AladinBookCrawler aladinBookCrawler,
                                        BandinLunisBookCrawler bandinLunisBookCrawler,
                                        KyoboBookRestTemplate kyoboBookRestTemplate) {

        this.bookFindAppService = bookFindAppService;
        this.bookStoreFinders = Arrays.asList(
                aladinBookCrawler,
                bandinLunisBookCrawler,
                kyoboBookRestTemplate
        );
    }

    public BookStore getBook(String isbn) {
        CompletableFuture<BookStore> bookFuture =
                CompletableFuture.supplyAsync(() -> bookFindAppService.getBook(isbn))
                .thenApplyAsync(x -> new BookStore(x, getBookStores(isbn)));

        return getFutureItem(bookFuture)
                .orElseThrow(() -> new NotFoundBookException(isbn));
    }

    private List<Store> getBookStores(String isbn) {
        List<Store> stores = new ArrayList<>();

        bookStoreFinders.stream()
                .map(x -> CompletableFuture.supplyAsync(() -> x.findByIsbn(isbn)))
                .collect(Collectors.toList())
                .forEach(x -> stores.addAll(getFutureItem(x).orElse(Collections.emptyList())));

        return stores;
    }

}
