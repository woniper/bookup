package io.bookup.book.api;

import io.bookup.book.app.FindBookAppService;
import io.bookup.book.domain.Book;
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
public class BookController {

    private final FindBookAppService findBookAppService;

    public BookController(FindBookAppService findBookAppService) {
        this.findBookAppService = findBookAppService;
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> findBooks(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(findBookAppService.getBook(isbn));
    }

    @GetMapping(value = "/{title}", params = {"page", "size"})
    public ResponseEntity<Page<Book>> findBooks(@PathVariable("title") String title,
                                                Pageable pageable) {
        return ResponseEntity.ok(findBookAppService.getBook(title, pageable));
    }
}
