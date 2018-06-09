package io.bookup.book.infra.crawler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import lombok.Getter;

/**
 * @author woniper
 */
@Getter
public class BookStore {

    @JsonIgnore
    public static final Collection<BookStore> EMPTY = Collections.emptySet();

    private String storeName;
    private String href;

    BookStore(String storeName, String href) {
        this.storeName = storeName;
        this.href = href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookStore bookStore = (BookStore) o;

        if (storeName != null ? !storeName.equals(bookStore.storeName) : bookStore.storeName != null) return false;
        return href != null ? href.equals(bookStore.href) : bookStore.href == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, href);
    }
}
