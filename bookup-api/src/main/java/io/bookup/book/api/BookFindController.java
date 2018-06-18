package io.bookup.book.api;

import io.bookup.book.api.NaverBookResponseDto.Item;
import io.bookup.book.app.BookStoreFindAppService;
import io.bookup.book.infra.Pageable;
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

    private final BookStoreFindAppService bookStoreFindAppService;
    private final NaverBookRestTemplate naverBookRestTemplate;

    public BookFindController(BookStoreFindAppService bookStoreFindAppService,
                              NaverBookRestTemplate naverBookRestTemplate) {

        this.bookStoreFindAppService = bookStoreFindAppService;
        this.naverBookRestTemplate = naverBookRestTemplate;
    }

    @GetMapping(value = "/stores/{isbn}")
    public ResponseEntity<?> findBookStore(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(bookStoreFindAppService.getBook(isbn));
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Item> findBooks(@PathVariable("isbn") String isbn) {
        NaverBook.Item item = naverBookRestTemplate.findByIsbn(isbn);
        return ResponseEntity.ok(new Item(item));
    }

    @GetMapping(value = "/{title}", params = {"page", "size"})
    public ResponseEntity<NaverBookResponseDto> findBooks(@PathVariable("title") String title, Pageable pageable) {
        NaverBook naverBook = naverBookRestTemplate.findByTitle(title, pageable);
        return ResponseEntity.ok(new NaverBookResponseDto(naverBook));
    }
}
