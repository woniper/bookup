package io.bookup.book.api.representation;

import io.bookup.book.domain.Book;
import java.util.List;
import lombok.Getter;

/**
 * @author woniper
 */
@Getter
public class PageableBook {

    private Pageable.Response pageable;

    private List<Book> books;

    public PageableBook(Pageable.Response pageable, List<Book> books) {
        this.pageable = pageable;
        this.books = books;
    }
}
