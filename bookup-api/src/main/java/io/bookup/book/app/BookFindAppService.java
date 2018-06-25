package io.bookup.book.app;

import io.bookup.book.domain.Book;
import io.bookup.book.domain.NotFoundBookException;
import io.bookup.book.infra.rest.NaverRestTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author woniper
 */
@Service
public class BookFindAppService {

    private final NaverRestTemplate naverBookRestTemplate;

    public BookFindAppService(NaverRestTemplate naverBookRestTemplate) {
        this.naverBookRestTemplate = naverBookRestTemplate;
    }

    public Book getBook(String isbn) {
        Assert.isTrue(StringUtils.isNumeric(isbn), "잘못된 isbn 입니다.");

        return naverBookRestTemplate.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundBookException(isbn));
    }

    public Page<Book> getBook(String title, Pageable pageable) {
        return naverBookRestTemplate.findByTitle(title, pageable);
    }
}

