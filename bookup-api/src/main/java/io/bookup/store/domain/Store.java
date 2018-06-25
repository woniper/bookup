package io.bookup.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import lombok.Getter;

/**
 * @author woniper
 */
@Getter
public class Store {

    @JsonIgnore
    public static final Collection<Store> EMPTY = Collections.emptyList();

    private String storeName;
    private String href;

    public Store(String storeName, String href) {
        this.storeName = storeName;
        this.href = href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Store store = (Store) o;

        if (storeName != null ? !storeName.equals(store.storeName) : store.storeName != null) return false;
        return href != null ? href.equals(store.href) : store.href == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, href);
    }
}
