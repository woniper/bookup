package io.bookup.book.app;

import io.bookup.book.domain.Book;
import io.bookup.book.domain.NotFoundBookException;
import io.bookup.book.domain.NaverBookClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author woniper
 */
@Slf4j
@Service
public class BookFindAppService {

    private final NaverBookClient naverBookRestTemplate;

    public BookFindAppService(NaverBookClient naverBookRestTemplate) {
        this.naverBookRestTemplate = naverBookRestTemplate;
    }

    @Cacheable(value = "book", key = "#isbn")
    public Book getBook(String isbn) {
        Assert.isTrue(StringUtils.isNumeric(isbn), "잘못된 isbn 입니다.");

        return naverBookRestTemplate.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundBookException(isbn));
    }

    @Cacheable(value = "book", key = "#title + #pageable.pageSize + #pageable.offset")
    public Page<Book> getBook(String title, Pageable pageable) {
        return naverBookRestTemplate.findByTitle(title, pageable);
    }
}

