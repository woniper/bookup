package io.bookup.store.api;

import io.bookup.store.app.BookStoreCompositeAppService;
import io.bookup.store.domain.BookStore;
import io.bookup.store.domain.Store.StoreType;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<BookStore> findBookStore(@PathVariable("isbn") String isbn,
                                                   @RequestParam(value = "stores", required = false) Set<StoreType> storeTypes) {
        return ResponseEntity.ok(bookStoreCompositeAppService.getBook(isbn, storeTypes));
    }
}
