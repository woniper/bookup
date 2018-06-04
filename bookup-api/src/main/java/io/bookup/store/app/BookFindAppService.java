package io.bookup.store.app;

import io.bookup.store.infra.crawler.AladinBookCrawler;
import io.bookup.store.infra.crawler.Book;
import io.bookup.store.infra.crawler.BookCrawlerComposite;
import io.bookup.store.infra.crawler.KyoboBookCrawler;
import org.springframework.stereotype.Service;

/**
 * @author woniper
 */
@Service
public class BookFindAppService {

    private final BookCrawlerComposite bookCrawler;

    public BookFindAppService(BookCrawlerComposite bookCrawlerComposite,
                              AladinBookCrawler aladinBookCrawler,
                              KyoboBookCrawler kyoboBookCrawler) {

        this.bookCrawler = bookCrawlerComposite
                .addBookCrawler(aladinBookCrawler)
                .addBookCrawler(kyoboBookCrawler);
    }

    public Book findByIsbn(String isbn) {
        return bookCrawler.findByIsbn(isbn);
    }

}
