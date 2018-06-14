package io.bookup.book.infra.crawler;

import io.bookup.book.infra.crawler.KyoboProperties.StoreList;
import io.bookup.test.utils.MockGenerator;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
public class KyoboBookCrawlerTests {

    private BookCrawler crawler;
    private BookCrawler notFoundCrawler;

    @Before
    public void setUp() throws Exception {
        KyoboProperties properties = new KyoboProperties();
        properties.setUrl("http://mockurl.com");
        properties.setStoreList(Arrays.asList(
                new StoreList("교보문고 : 광화문점", "01"),
                new StoreList("교보문고 : 강남점", "15")));


        this.crawler = new KyoboBookCrawler(
                MockGenerator.mockRestTemplate("html/kyobo.html"),
                properties);

        this.notFoundCrawler = new KyoboBookCrawler(
                MockGenerator.mockRestTemplate("html/notFoundKyobo.html"),
                properties);
    }

    @Test
    public void testFindBook() throws Exception {
        // given, when
        Book book = crawler.findByIsbn("123123");
        Collection<BookStore> bookStores = book.getBookStores();

        // then
        assertThat(book.getTitle()).contains("스프링");
        assertThat(book.getDescription()).contains("김범준");
        assertThat(bookStores.stream().findFirst().get().getStoreName()).contains("교보문고");
    }

    @Test
    public void testNotFoundFindBook() throws Exception {
        // given, when
        Book notFoundBook = notFoundCrawler.findByIsbn("12879281");

        // then
        assertThat(notFoundBook).isNull();
    }
}
