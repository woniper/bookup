package io.bookup.store.domain;

import io.bookup.book.domain.Book;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author woniper
 */
@Getter
@NoArgsConstructor
public class BookStore {

    private Book book;
    private List<Store> stores;

    public BookStore(Book book, List<Store> stores) {
        this.book = book;
        this.stores = stores;
    }
}
