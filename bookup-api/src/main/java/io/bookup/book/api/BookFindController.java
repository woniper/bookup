package io.bookup.book.api;

import io.bookup.book.api.representation.NaverBookResponseDto;
import io.bookup.book.api.representation.NaverBookResponseDto.Item;
import io.bookup.book.app.BookStoreCompositeAppService;
import io.bookup.book.domain.NotFoundBookException;
import io.bookup.book.infra.rest.NaverBook;
import io.bookup.book.infra.rest.NaverBookRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author woniper
 */
@RestController
@RequestMapping("/books")
public class BookFindController {

    private final BookStoreCompositeAppService bookStoreCompositeAppService;
    private final NaverBookRestTemplate naverBookRestTemplate;

    public BookFindController(BookStoreCompositeAppService bookStoreCompositeAppService,
                              NaverBookRestTemplate naverBookRestTemplate) {

        this.bookStoreCompositeAppService = bookStoreCompositeAppService;
        this.naverBookRestTemplate = naverBookRestTemplate;
    }

    @GetMapping(value = "/stores/{isbn}")
    public ResponseEntity<?> findBookStore(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(bookStoreCompositeAppService.getBook(isbn));
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Item> findBooks(@PathVariable("isbn") String isbn) {
        NaverBook.Item item = naverBookRestTemplate.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundBookException(isbn));
        return ResponseEntity.ok(new Item(item));
    }

    @GetMapping(value = "/{title}", params = {"page", "size"})
    public ResponseEntity<NaverBookResponseDto> findBooks(@PathVariable("title") String title,
                                                          Pageable pageable) {
        NaverBook naverBook = naverBookRestTemplate.findByTitle(title, pageable)
                .orElseThrow(() -> new NotFoundBookException(title));
        return ResponseEntity.ok(new NaverBookResponseDto(naverBook));
    }
}
