package io.bookup.book.infra.crawler;

import io.bookup.book.domain.BookStore;
import io.bookup.book.infra.BookFinder;
import java.nio.charset.Charset;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
public class AladinBookCrawlerTests {

    private BookFinder<List<BookStore>> crawler;
    private BookFinder<List<BookStore>> notFoundCrawler;

    private String isbn = "12398798243";

    @Before
    public void setUp() throws Exception {
        this.crawler = new AladinBookCrawler(
                "http://mockurl.com",
                MockGenerator.restTemplate("html/aladin.html", Charset.forName("euc-kr")));

        this.notFoundCrawler = new AladinBookCrawler(
                "http://mockurl.com",
                MockGenerator.restTemplate("html/notFoundAladin.html", Charset.forName("euc-kr")));
    }

    @Test
    public void testFindBook() throws Exception {
        List<BookStore> bookStores = crawler.findByIsbn(isbn);

        assertThat(bookStores).isNotEmpty().hasSize(1);
        assertThat(bookStores.stream().findFirst().get().getStoreName()).contains("알라딘");
    }

    @Test
    public void testNotFoundFindBook() throws Exception {
        List<BookStore> book = notFoundCrawler.findByIsbn(isbn);
        assertThat(book).isNull();
    }

}
