package io.bookup.store.api;

import io.bookup.store.app.BookFindAppService;
import io.bookup.store.infra.crawler.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author woniper
 */
@RestController
public class BookFindController {

    private final BookFindAppService bookFindAppService;

    public BookFindController(BookFindAppService bookFindAppService) {
        this.bookFindAppService = bookFindAppService;
    }

    @GetMapping(value = "/books", params = "isbn")
    public ResponseEntity<?> findBooks(@RequestParam("isbn") String isbn) {
        Book book = bookFindAppService.findByIsbn(isbn);
        return ResponseEntity.ok(book);
    }
}
