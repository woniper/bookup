package io.bookup.book.infra.crawler;

import io.bookup.book.domain.Store;
import io.bookup.book.infra.BookFinder;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class AladinBookCrawler implements BookFinder<List<Store>> {

    private final String HTML_CLASS_NAME_SS_BOOK_BOX = "ss_book_box";
    private final String HTML_CLASS_NAME_USED_SHOP_OFF_TEXT3 = "usedshop_off_text3";
    private final String HTML_ATTRIBUTE_NAME_HREF = "href";

    private final String url;
    private final RestTemplate restTemplate;

    AladinBookCrawler(@Value("${bookup.crawler.aladin.url}") String url,
                      RestTemplate restTemplate) {

        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Store> findByIsbn(String isbn) {
        Element bodyElement = getBodyElement(createUrl(isbn));

        return findBookStores(bodyElement);
    }

    private List<Store> findBookStores(Element element) {
        if (Objects.isNull(element)) return Collections.emptyList();
        if (hasNotBook(element)) return Collections.emptyList();

        Elements boxElements = element.getElementsByClass(HTML_CLASS_NAME_SS_BOOK_BOX);

        if (CollectionUtils.isEmpty(boxElements)) return Collections.emptyList();

        return getBookStoreElement(boxElements).stream()
                .map(x -> new Store("알라딘 : " + x.text(), getBookStoreHref(x)))
                .collect(Collectors.toList());
    }

    private boolean hasNotBook(Element bodyElement) {
        if (Objects.isNull(bodyElement)) return false;

        return !bodyElement.getAllElements().hasClass(HTML_CLASS_NAME_SS_BOOK_BOX);
    }

    private String createUrl(String isbn) {
        return String.format(this.url, isbn);
    }

    private String getBookStoreHref(Element element) {
        return element.attr(HTML_ATTRIBUTE_NAME_HREF);
    }

    private Elements getBookStoreElement(Elements elements) {
        Element bookElement = elements.first();
        return bookElement.getElementsByClass(HTML_CLASS_NAME_USED_SHOP_OFF_TEXT3);
    }

    private Element getBodyElement(String url) {
        String html = restTemplate.getForObject(url, String.class);
        return Jsoup.parse(html).body();
    }
}
