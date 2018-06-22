package io.bookup.book.domain;

import io.bookup.book.api.Pageable;
import io.bookup.book.infra.rest.NaverBook;
import io.bookup.book.infra.rest.NaverBookRestTemplate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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

    public List<Book> getBook(String title, Pageable pageable) {
        return naverBookRestTemplate.findByTitle(title, pageable)
                .map(NaverBook::getItems)
                .orElse(Collections.emptyList()).stream()
                .map(this::mapBook)
                .collect(Collectors.toList());
    }

    private Book mapBook(NaverBook.Item item) {
        return Book.builder()
                .title(item.getTitle())
                .description(item.getDescription())
                .link(item.getLink())
                .image(item.getDescription())
                .author(item.getAuthor())
                .price(item.getPrice())
                .publisher(item.getPublisher())
                .publishDate(item.getPubdate())
                .isbn(item.getIsbn())
                .build();
    }
}

