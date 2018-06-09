package io.bookup.book.app;

import io.bookup.book.infra.crawler.AladinBookCrawler;
import io.bookup.book.infra.crawler.Book;
import io.bookup.book.infra.crawler.BookCrawlerComposite;
import io.bookup.book.infra.crawler.KyoboBookCrawler;
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

    public Book getBook(String isbn) {
        return bookCrawler.findByIsbn(isbn);
    }

}
