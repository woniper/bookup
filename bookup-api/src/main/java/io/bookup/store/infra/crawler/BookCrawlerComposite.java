package io.bookup.store.infra.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * @author woniper
 */
@Component
public class BookCrawlerComposite implements BookCrawler {

    private List<BookCrawler> bookCrawlers = new ArrayList<>();

    public BookCrawlerComposite addBookCrawler(BookCrawler bookCrawler) {
        this.bookCrawlers.add(bookCrawler);
        return this;
    }

    @Override
    public Book findByIsbn(String isbn) {
        List<Book> books = bookCrawlers.stream()
                .map(x -> x.findByIsbn(isbn))
                .collect(Collectors.toList());

        return mergeBook(books);
    }

    private Book mergeBook(List<Book> books) {
        Book book = new Book();
        books.forEach(book::merge);

        return book;
    }
}
