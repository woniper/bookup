package io.bookup.book.infra;

/**
 * @author woniper
 */
public interface BookFinder<T> {

    T findByIsbn(String isbn);

}