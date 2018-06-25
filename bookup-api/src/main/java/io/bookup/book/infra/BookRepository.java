package io.bookup.book.infra;

import io.bookup.book.domain.Book;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author woniper
 */
public interface BookRepository {

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findByTitle(String title, Pageable pageable);

}
