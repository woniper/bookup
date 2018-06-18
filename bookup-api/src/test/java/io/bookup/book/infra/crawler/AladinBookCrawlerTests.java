package io.bookup.book.infra.crawler;

import io.bookup.book.domain.Book;
import io.bookup.book.infra.BookFinder;
import io.bookup.book.domain.BookStore;
import java.nio.charset.Charset;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
public class AladinBookCrawlerTests {

    private BookFinder<Book> crawler;
    private BookFinder<Book> notFoundCrawler;

    private String isbn = "12398798243";

    @Before
    public void setUp() throws Exception {
        this.crawler = new AladinBookCrawler(
                "http://mockurl.com",
                MockGenerator.mockRestTemplate("html/aladin.html", Charset.forName("euc-kr")));

        this.notFoundCrawler = new AladinBookCrawler(
                "http://mockurl.com",
                MockGenerator.mockRestTemplate("html/notFoundAladin.html", Charset.forName("euc-kr")));
    }

    @Test
    public void testFindBook() throws Exception {
        Book book = crawler.findByIsbn(isbn);
        Collection<BookStore> bookStores = book.getBookStores();

        assertThat(book.getTitle()).contains("스프링");
        assertThat(book.getDescription()).contains("김범준");
        assertThat(book.getBookStores().size()).isEqualTo(1);
        assertThat(bookStores).isNotEmpty().hasSize(1);
        assertThat(bookStores.stream().findFirst().get().getStoreName()).contains("알라딘");
    }

    @Test
    public void testNotFoundFindBook() throws Exception {
        Book book = notFoundCrawler.findByIsbn(isbn);
        assertThat(book).isNull();
    }

}
