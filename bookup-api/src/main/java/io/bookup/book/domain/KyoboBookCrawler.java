package io.bookup.book.domain;

import io.bookup.book.infra.BookRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class KyoboBookCrawler implements BookRepository {

    private final KyoboBookProperties properties;
    private final RestTemplate restTemplate;

    public KyoboBookCrawler(KyoboBookProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        Element bodyElement = getBodyElement(properties.getIsbnUrl(isbn));
        Elements titleElements = bodyElement.getElementsByClass("box_detail_point");

        if (titleElements.isEmpty()) {
            return Optional.empty();
        }

        String title = titleElements
                .first()
                .getElementsByClass("title")
                .first()
                .text();

        String description = bodyElement.getElementsByClass("box_detail_article")
                .first()
                .text();

        String image = bodyElement.getElementsByClass("box_detail_cover")
                .first()
                .getElementsByClass("cover")
                .first()
                .getElementsByTag("img")
                .attr("src");

        String author = bodyElement.getElementsByClass("detail_author")
                .first()
                .text();

        String price = bodyElement.getElementsByClass("org_price")
                .first()
                .text();

        Book book = Book.builder()
                .title(title)
                .description(description)
                .image(image)
                .author(author)
                .price(price)
                .isbn(isbn)
                .build();

        return Optional.ofNullable(book);
    }

    @Override
    public Page<Book> findByTitle(String title, Pageable pageable) {
        Element bodyElement = getBodyElement(properties.getListUrl(title, pageable.getPageNumber(), pageable.getPageSize()));
        Elements bookBodyElements = bodyElement.getElementsByClass("type_list");

        if (bookBodyElements.isEmpty()) {
            return new PageImpl<>(Collections.emptyList());
        }

        Elements booksElements = bookBodyElements.first()
                .getElementsByTag("tbody")
                .first()
                .getElementsByTag("tr");

        if (bookBodyElements.isEmpty()) {
            return new PageImpl<>(Collections.emptyList());
        }

        List<Book> books = booksElements.stream()
                .map(x -> {
                    String bookTitle =  x.getElementsByClass("title")
                            .first()
                            .getElementsByTag("a")
                            .first()
                            .text();

                    String image = x.getElementsByClass("cover")
                            .first()
                            .getElementsByTag("img")
                            .attr("src");

                    String author = x.getElementsByClass("author")
                            .first()
                            .getElementsByTag("a")
                            .text();

                    String price = x.getElementsByClass("org_price")
                            .first()
                            .text();

                    String isbn = x.getElementsByClass("image")
                            .first()
                            .getElementsByTag("input")
                            .first()
                            .attr("value");

                    return Book.builder()
                            .title(bookTitle)
                            .description("")
                            .image(image)
                            .author(author)
                            .price(price)
                            .isbn(isbn)
                            .build();
                })
                .collect(Collectors.toList());

        long total = getTotal(bodyElement);

        return new PageImpl<>(books, pageable, total);
    }

    private long getTotal(Element bodyElement) {
        String page = bodyElement.getElementsByClass("title_search_basic")
                .first()
                .getElementsByTag("small")
                .first()
                .text();

        int startIndex = page.indexOf("총");
        int endIndex = page.indexOf("건");

        String totalString = page.substring(startIndex + 1, endIndex)
                .replace(",", "")
                .trim();

        return Long.valueOf(totalString);
    }

    private Element getBodyElement(String url) {
        String html = restTemplate.getForObject(url, String.class);
        return Jsoup.parse(html).body();
    }

}
