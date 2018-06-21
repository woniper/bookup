package io.bookup.book.domain;

import io.bookup.book.infra.rest.NaverBook;
import io.bookup.book.infra.rest.NaverBookRestTemplate;
import org.springframework.stereotype.Service;

/**
 * @author woniper
 */
@Service
public class BookFindService {

    private final NaverBookRestTemplate naverBookRestTemplate;

    public BookFindService(NaverBookRestTemplate naverBookRestTemplate) {
        this.naverBookRestTemplate = naverBookRestTemplate;
    }

    public Book getBook(String isbn) {
        return mapBook(naverBookRestTemplate.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundBookException(isbn)));
    }

    private Book mapBook(NaverBook.Item item) {
        return new Book(item.getTitle(), item.getDescription());
    }
}

