package io.bookup.store.app;

import io.bookup.store.domain.BookStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author woniper
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BookStoreCompositeAppServiceTest {

    @Autowired
    private BookStoreCompositeAppService service;

    @Test(expected = IllegalArgumentException.class)
    public void getBook_잘못된_isbn() {
        // given
        String wrongIsbn = "9788994492032a";

        // when
        service.getBook(wrongIsbn);

        // then
        fail();
    }

    @Test
    public void getBook() {
        // given
        String isbn = "9788994492032";

        // when
        BookStore book = service.getBook(isbn);

        // then
        assertThat(book).isNotNull();
        assertThat(book.getBook()).isNotNull();
        assertThat(book.getBook().getTitle())
                .contains("Java")
                .contains("정석");
        assertThat(book.getStores()).isNotNull();
    }
}
