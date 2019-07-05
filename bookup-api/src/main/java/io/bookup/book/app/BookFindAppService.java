package io.bookup.book.app;

import io.bookup.book.domain.Book;
import io.bookup.book.domain.NotFoundBookException;
import io.bookup.book.infra.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private final BookRepository bookRepository;

    public BookFindAppService(@Qualifier("kyoboBookCrawler") BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Cacheable(value = "book", key = "#isbn")
    public Book getBook(String isbn) {
        Assert.isTrue(StringUtils.isNumeric(isbn), "잘못된 isbn 입니다.");

        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundBookException(isbn));
    }

    @Cacheable(value = "book", key = "#title + #pageable.pageSize + #pageable.offset")
    public Page<Book> getBook(String title, Pageable pageable) {
        return bookRepository.findByTitle(title, pageable);
    }
}

