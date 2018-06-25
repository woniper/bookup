package io.bookup.store.api;

import io.bookup.store.domain.BookStore;
import io.bookup.store.app.BookStoreCompositeAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author woniper
 */
@RestController
@RequestMapping("/stores")
public class StoreBookController {

    private final BookStoreCompositeAppService bookStoreCompositeAppService;

    public StoreBookController(BookStoreCompositeAppService bookStoreCompositeAppService) {
        this.bookStoreCompositeAppService = bookStoreCompositeAppService;
    }

    @GetMapping(value = "/books/{isbn}")
    public ResponseEntity<BookStore> findBookStore(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(bookStoreCompositeAppService.getBook(isbn));
    }
}
