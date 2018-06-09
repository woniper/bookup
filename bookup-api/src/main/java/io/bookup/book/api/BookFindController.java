package io.bookup.book.api;

import io.bookup.book.app.BookFindAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping(value = "/books/{isbn}")
    public ResponseEntity<?> findBooks(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(bookFindAppService.getBook(isbn));
    }
}
