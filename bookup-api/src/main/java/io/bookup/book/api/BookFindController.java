package io.bookup.book.api;

import io.bookup.book.app.BookStoreCompositeAppService;
import io.bookup.book.domain.BookFindService;
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
    private final BookFindService bookFindService;

    public BookFindController(BookStoreCompositeAppService bookStoreCompositeAppService,
                              BookFindService bookFindService) {

        this.bookStoreCompositeAppService = bookStoreCompositeAppService;
        this.bookFindService = bookFindService;
    }

    @GetMapping(value = "/stores/{isbn}")
    public ResponseEntity<?> findBookStore(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(bookStoreCompositeAppService.getBook(isbn));
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<?> findBooks(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(bookFindService.getBook(isbn));
    }

    @GetMapping(value = "/{title}", params = {"page", "size"})
    public ResponseEntity<?> findBooks(@PathVariable("title") String title, Pageable pageable) {
        return ResponseEntity.ok(bookFindService.getBook(title, pageable));
    }
}
