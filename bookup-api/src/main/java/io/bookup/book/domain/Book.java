package io.bookup.book.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author woniper
 */
@NoArgsConstructor
@Getter
public class Book {

    private String title;
    private String description;
    private Collection<BookStore> bookStores = new ArrayList<>();

    public Book(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Book(String title, String description, Collection<BookStore> bookStores) {
        this(title, description);
        this.bookStores = bookStores;
    }

    public Book merge(Collection<? extends BookStore> bookStores) {
        if (Objects.nonNull(bookStores) && !bookStores.isEmpty()) {
            this.bookStores.addAll(bookStores);
        }
        return this;
    }
}
