package io.bookup.book.infra.crawler;

import io.bookup.book.infra.BookFinder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
public class BookCrawlerCompositeTests {

    @Test
    public void testAladinBookCrawler() throws Exception {
        // given
        AladinBookCrawler aladinBookCrawler = new AladinBookCrawler(
                "http://mockurl.com",
                MockGenerator.mockRestTemplate("html/kyobo.html"));

        BookFinder<Book> crawler = new BookCrawlerComposite()
                .addBookCrawler(aladinBookCrawler);

        // when
        Book book = crawler.findByIsbn("3450980");

        // then
        assertThat(book).isNotNull();
        book.getBookStores()
                .forEach(bookStore -> assertThat(bookStore.getStoreName())
                        .contains("알라딘")
                        .doesNotContain("교보문고"));
    }
}
