package io.bookup.book.infra.crawler;

import io.bookup.book.app.BookStoreCompositeAppService;
import io.bookup.book.domain.Book;
import java.nio.charset.Charset;
import org.junit.Test;

import static io.bookup.book.infra.crawler.MockGenerator.kyoboBookRestTemplate;
import static io.bookup.book.infra.crawler.MockGenerator.kyoboProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
public class BookStoreCompositeAppServiceTests {

    @Test
    public void testAladinBookCrawler() throws Exception {
        // given
        AladinBookCrawler bookCrawler = new AladinBookCrawler(
                "http://mockurl.com",
                MockGenerator.mockRestTemplate("html/aladin.html", Charset.forName("euc-kr")));

        BookStoreCompositeAppService service =
                new BookStoreCompositeAppService(bookCrawler, kyoboBookRestTemplate(), kyoboProperties());

        // when
        Book book = service.getBook("3450980");

        // then
        assertThat(book).isNotNull();
        assertThat(book.getBookStores()).hasSize(4);
    }
}
