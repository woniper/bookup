package io.bookup.book.app;

import io.bookup.book.domain.Book;
import io.bookup.book.domain.BookFindService;
import io.bookup.book.domain.Store;
import io.bookup.book.domain.NotFoundBookException;
import io.bookup.book.infra.crawler.AladinBookCrawler;
import io.bookup.book.infra.crawler.BandinLunisBookCrawler;
import io.bookup.book.infra.rest.KyoboBookRestTemplate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * @author woniper
 */
@RunWith(MockitoJUnitRunner.class)
public class StoreCompositeAppServiceTest {

    private final String isbn = "4689347598347";

    @Mock
    private BookFindService bookFindService;

    @Mock
    private AladinBookCrawler aladinBookCrawler;

    @Mock
    private KyoboBookRestTemplate kyoboBookRestTemplate;

    @Mock
    private BandinLunisBookCrawler bandinLunisBookCrawler;

    private BookStoreCompositeAppService service;

    @Before
    public void setUp() throws Exception {
        this.service = new BookStoreCompositeAppService(
                bookFindService,
                aladinBookCrawler,
                bandinLunisBookCrawler,
                kyoboBookRestTemplate);
    }

    @Test(expected = NotFoundBookException.class)
    public void getBook_is_null() {
        // given
        when(bookFindService.getBook(isbn)).thenReturn(null);

        // when
        service.getBook(isbn);

        // then
        fail();
    }

    @Test
    public void getBook_교보만_조회() {
        // given
        when(bookFindService.getBook(isbn))
                .thenReturn(Book.builder()
                        .title("test title")
                        .description("test description")
                        .build());

        when(kyoboBookRestTemplate.findByIsbn(isbn))
                .thenReturn(Arrays.asList(
                        new Store("test storeName1", "http://kyobo.example.com"),
                        new Store("test storeName2", "http://kyobo.example.com"),
                        new Store("test storeName3", "http://kyobo.example.com")
                ));

        when(aladinBookCrawler.findByIsbn(isbn)).thenReturn(Collections.emptyList());

        when(bandinLunisBookCrawler.findByIsbn(isbn)).thenReturn(Collections.emptyList());

        // when
        Book book = service.getBook(isbn);

        // then
        assertThat(book).isNotNull();
        assertThat(book.getStores()).hasSize(3);
    }

    @Test
    public void getBook_알리딘만_조회() {
        // given
        when(bookFindService.getBook(isbn))
                .thenReturn(Book.builder()
                        .title("test title")
                        .description("test description")
                        .build());

        when(aladinBookCrawler.findByIsbn(isbn))
                .thenReturn(Arrays.asList(
                        new Store("test storeName1", "http://aladin.example.com"),
                        new Store("test storeName2", "http://aladin.example.com"),
                        new Store("test storeName3", "http://aladin.example.com")
                ));

        when(kyoboBookRestTemplate.findByIsbn(isbn)).thenReturn(Collections.emptyList());

        when(bandinLunisBookCrawler.findByIsbn(isbn)).thenReturn(Collections.emptyList());

        // when
        Book book = service.getBook(isbn);

        // then
        assertThat(book).isNotNull();
        assertThat(book.getStores()).hasSize(3);
    }

    @Test
    public void getBook_반디만_조회() {
        // given
        when(bookFindService.getBook(isbn))
                .thenReturn(Book.builder()
                        .title("test title")
                        .description("test description")
                        .build());

        when(bandinLunisBookCrawler.findByIsbn(isbn))
                .thenReturn(Arrays.asList(
                        new Store("test storeName1", "http://bandi.example.com"),
                        new Store("test storeName2", "http://bandi.example.com"),
                        new Store("test storeName3", "http://bandi.example.com")
                ));

        when(kyoboBookRestTemplate.findByIsbn(isbn)).thenReturn(Collections.emptyList());

        when(aladinBookCrawler.findByIsbn(isbn)).thenReturn(Collections.emptyList());

        // when
        Book book = service.getBook(isbn);

        // then
        assertThat(book).isNotNull();
        assertThat(book.getStores()).hasSize(3);
    }

    @Test
    public void getBook_모든_BookStores_조회() {
        // given
        when(bookFindService.getBook(isbn))
                .thenReturn(Book.builder()
                        .title("test title")
                        .description("test description")
                        .build());

        when(bandinLunisBookCrawler.findByIsbn(isbn))
                .thenReturn(Arrays.asList(
                        new Store("test storeName1", "http://bandi.example.com"),
                        new Store("test storeName2", "http://bandi.example.com"),
                        new Store("test storeName3", "http://bandi.example.com")
                ));

        when(kyoboBookRestTemplate.findByIsbn(isbn))
                .thenReturn(Arrays.asList(
                        new Store("test storeName4", "http://kyobo.example.com"),
                        new Store("test storeName5", "http://kyobo.example.com"),
                        new Store("test storeName6", "http://kyobo.example.com")
                ));

        when(aladinBookCrawler.findByIsbn(isbn))
                .thenReturn(Arrays.asList(
                        new Store("test storeName7", "http://aladin.example.com"),
                        new Store("test storeName8", "http://aladin.example.com"),
                        new Store("test storeName9", "http://aladin.example.com")
                ));

        // when
        Book book = service.getBook(isbn);

        // then
        assertThat(book).isNotNull();
        assertThat(book.getStores()).hasSize(9);
    }
}
