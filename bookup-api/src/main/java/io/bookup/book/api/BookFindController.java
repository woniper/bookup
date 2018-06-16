package io.bookup.book.api;

import io.bookup.book.app.BookStoreFindAppService;
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
    public ResponseEntity<?> findBooks(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(naverBookRestTemplate.findByIsbn(isbn));
    }
}
