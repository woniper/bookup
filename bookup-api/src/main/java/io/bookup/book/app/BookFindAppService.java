package io.bookup.book.app;

import io.bookup.book.api.representation.Pageable;
import io.bookup.book.api.representation.PageableBook;
import io.bookup.book.domain.Book;
import io.bookup.book.domain.NotFoundBookException;
import io.bookup.book.infra.rest.NaverBook;
import io.bookup.book.infra.rest.NaverBookRestTemplate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * @author woniper
 */
@Service
public class BookFindAppService {

    private final NaverBookRestTemplate naverBookRestTemplate;

    public BookFindAppService(NaverBookRestTemplate naverBookRestTemplate) {
        this.naverBookRestTemplate = naverBookRestTemplate;
    }

    public Book getBook(String isbn) {
        return mapBook(naverBookRestTemplate.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundBookException(isbn)));
    }

    public PageableBook getBook(String title, Pageable pageable) {
        NaverBook naverBook = naverBookRestTemplate.findByTitle(title, pageable)
                .orElseThrow(() -> new NotFoundBookException(title));

        return new PageableBook(
                new Pageable.Response(
                        naverBook.getStart() - 1,
                        naverBook.getDisplay(),
                        naverBook.getTotal()),
                naverBook.getItems().stream()
                        .map(this::mapBook)
                        .collect(Collectors.toList()));
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

