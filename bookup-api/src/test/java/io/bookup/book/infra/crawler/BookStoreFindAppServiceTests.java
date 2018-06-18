package io.bookup.book.infra.crawler;

import io.bookup.book.app.BookStoreFindAppService;
import io.bookup.book.domain.Book;
import java.nio.charset.Charset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
public class BookStoreFindAppServiceTests {

    @Test
    public void testAladinBookCrawler() throws Exception {
        // given
        AladinBookCrawler bookCrawler = new AladinBookCrawler(
                "http://mockurl.com",
                MockGenerator.mockRestTemplate("html/aladin.html", Charset.forName("euc-kr")));

        BookStoreFindAppService service = new BookStoreFindAppService(bookCrawler, MockGenerator.kyoboBookRestTemplate());

        // when
        Book book = service.getBook("3450980");

        // then
        assertThat(book).isNotNull();
        assertThat(book.getBookStores()).hasSize(4);
    }
}
