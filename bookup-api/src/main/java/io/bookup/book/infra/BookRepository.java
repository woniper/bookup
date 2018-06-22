package io.bookup.book.infra;

import io.bookup.book.api.representation.Pageable;

/**
 * @author woniper
 */
public interface BookRepository<T> {

    T findByTitle(String title, Pageable pageable);

}
