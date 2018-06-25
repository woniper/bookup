package io.bookup.store.app;

import io.bookup.book.app.BookFindAppService;
import io.bookup.book.domain.NotFoundBookException;
import io.bookup.store.domain.BookStore;
import io.bookup.store.domain.Store;
import io.bookup.store.infra.StoreRepository;
import io.bookup.store.infra.crawler.AladinCrawler;
import io.bookup.store.infra.crawler.BandinLunisCrawler;
import io.bookup.store.infra.rest.KyoboRestTemplate;
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
    private final List<StoreRepository> bookStoreFinders;

    public BookStoreCompositeAppService(BookFindAppService bookFindAppService,
                                        AladinCrawler aladinBookCrawler,
                                        BandinLunisCrawler bandinLunisBookCrawler,
                                        KyoboRestTemplate kyoboBookRestTemplate) {

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
