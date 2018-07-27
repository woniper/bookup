package io.bookup.library.api;

import io.bookup.library.domain.Library;
import io.bookup.library.infra.rest.NationalLibraryRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author woniper
 */
@RestController
@RequestMapping("/libraries")
public class LibraryController {

    private final NationalLibraryRestTemplate libraryRestTemplate;

    public LibraryController(NationalLibraryRestTemplate libraryRestTemplate) {
        this.libraryRestTemplate = libraryRestTemplate;
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Library> findLibraries(@PathVariable("isbn") String isbn) {
        return ResponseEntity.ok(libraryRestTemplate.findByIsbn(isbn));
    }
}
