package io.bookup.book.infra.crawler;

import io.bookup.book.domain.Store;
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

    private BookFinder<List<Store>> crawler;
    private BookFinder<List<Store>> notFoundCrawler;

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
        List<Store> stores = crawler.findByIsbn(isbn);

        assertThat(stores).isNotEmpty().hasSize(1);
        assertThat(stores.stream().findFirst().get().getStoreName()).contains("알라딘");
    }

    @Test
    public void testNotFoundFindBook() throws Exception {
        List<Store> book = notFoundCrawler.findByIsbn(isbn);
        assertThat(book).isEmpty();
    }

}
