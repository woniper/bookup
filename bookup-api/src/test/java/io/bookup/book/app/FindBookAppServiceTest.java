package io.bookup.book.app;

import io.bookup.book.domain.Book;
import io.bookup.book.domain.NotFoundBookException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author woniper
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FindBookAppServiceTest {

    @Autowired
    private FindBookAppService service;

    @Test(expected = IllegalArgumentException.class)
    public void getBook_null_isbn() {
        // when
        service.getBook(null);

        // then
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBook_잘못된_isbn() {
        // given
        String wrongIsbn = "9788994492032a";

        // when
        service.getBook(wrongIsbn);

        // then
        fail();
    }

    @Test(expected = NotFoundBookException.class)
    public void getBook_not_found_exception() {
        // given
        String isbn = "9788994492032123";

        // when
        service.getBook(isbn);

        // then
        fail();
    }

    @Test
    public void getBookByIsbn() {
        // given
        String isbn = "9788994492032";

        // when
        Book book = service.getBook(isbn);

        // then
        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isNotEmpty();
    }

    @Test
    public void getBookByTitle() {
        // given
        String title = "자바의";
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Book> books = service.getBook(title, pageable);

        // then
        assertThat(books).isNotEmpty();
        assertThat(books.getSize()).isEqualTo(20);
    }
}
