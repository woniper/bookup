package io.bookup.book.app;

import io.bookup.book.domain.Book;
import io.bookup.book.infra.crawler.AladinBookCrawler;
import io.bookup.book.infra.rest.KyoboBookRestTemplate;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author woniper
 */
@Service
public class BookStoreFindAppService {

    private final AladinBookCrawler aladinBookCrawler;
    private final KyoboBookRestTemplate kyoboBookRestTemplate;

    public BookStoreFindAppService(AladinBookCrawler aladinBookCrawler,
                                   KyoboBookRestTemplate kyoboBookRestTemplate) {

        this.aladinBookCrawler = aladinBookCrawler;
        this.kyoboBookRestTemplate = kyoboBookRestTemplate;
    }

    public Book getBook(String isbn) {
        return mergeBook(Arrays.asList(
                aladinBookCrawler.findByIsbn(isbn),
                kyoboBookRestTemplate.findByIsbn(isbn)
        ));
    }

    private Book mergeBook(List<Book> books) {
        Book book = new Book();
        books.forEach(book::merge);

        return book;
    }
}
