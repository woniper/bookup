package io.bookup.book.app;

import io.bookup.book.domain.Book;
import io.bookup.book.domain.BookFindService;
import io.bookup.book.domain.BookStore;
import io.bookup.book.domain.NotFoundBookException;
import io.bookup.book.infra.BookFinder;
import io.bookup.book.infra.crawler.AladinBookCrawler;
import io.bookup.book.infra.crawler.BandinLunisBookCrawler;
import io.bookup.book.infra.rest.KyoboBookRestTemplate;
import io.bookup.common.utils.FutureUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * @author woniper
 */
@Service
public class BookStoreCompositeAppService {

    private final BookFindService bookFindService;
    private final List<BookFinder<List<BookStore>>> bookStoreFinders;

    public BookStoreCompositeAppService(BookFindService bookFindService,
                                        AladinBookCrawler aladinBookCrawler,
                                        BandinLunisBookCrawler bandinLunisBookCrawler,
                                        KyoboBookRestTemplate kyoboBookRestTemplate) {

        this.bookFindService = bookFindService;
        this.bookStoreFinders = Arrays.asList(
                aladinBookCrawler,
                bandinLunisBookCrawler,
                kyoboBookRestTemplate
        );
    }

    public Book getBook(String isbn) {
        CompletableFuture<Book> bookFuture =
                CompletableFuture.supplyAsync(() -> bookFindService.getBook(isbn))
                .thenApplyAsync(x -> x.merge(getBookStores(isbn)));

        return FutureUtils.getFutureItem(bookFuture)
                .orElseThrow(() -> new NotFoundBookException(isbn));
    }

    private List<BookStore> getBookStores(String isbn) {
        List<BookStore> bookStores = new ArrayList<>();

        bookStoreFinders.stream()
                .map(x -> CompletableFuture.supplyAsync(() -> x.findByIsbn(isbn)))
                .collect(Collectors.toList())
                .forEach(x -> bookStores.addAll(FutureUtils.getFutureItem(x).orElse(Collections.emptyList())));

        return bookStores;
    }

}
