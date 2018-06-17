package io.bookup.book.infra;

/**
 * @author woniper
 */
public interface BookRepository<T> {

    T findByTitle(String title, Pageable pageable);

}
