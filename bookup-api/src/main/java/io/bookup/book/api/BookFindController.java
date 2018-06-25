package io.bookup.book.api;

import io.bookup.book.app.BookFindAppService;
import io.bookup.store.app.BookStoreCompositeAppService;
import io.bookup.book.domain.Book;
import io.bookup.book.domain.BookStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final BookFindAppService bookFindAppService;

    public BookFindController(BookStoreCompositeAppService bookStoreCompositeAppService,
                              BookFindAppService bookFindAppService) {

        this.bookStoreCompositeAppService = bookStoreCompositeAppService;
        this.bookFindAppService = bookFindAppService;
    }

    @GetMapping(value = "/stores/{isbn}")
    public ResponseEntity<BookStore> findBookStore(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(bookStoreCompositeAppService.getBook(isbn));
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> findBooks(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(bookFindAppService.getBook(isbn));
    }

    @GetMapping(value = "/{title}", params = {"page", "size"})
    public ResponseEntity<Page<Book>> findBooks(@PathVariable("title") String title,
                                                Pageable pageable) {
        return ResponseEntity.ok(bookFindAppService.getBook(title, pageable));
    }
}
