package io.bookup.book.infra.crawler;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
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
public class AladinBookCrawler implements BookCrawler {

    private final String HTML_CLASS_NAME_SS_BOOK_BOX = "ss_book_box";
    private final String HTML_CLASS_NAME_SS_BOOK_LIST = "ss_book_list";
    private final String HTML_TAG_NAME_LI = "li";
    private final String HTML_CLASS_NAME_BO3 = "bo3";
    private final String HTML_CLASS_NAME_SS_F_G2 = "ss_f_g2";
    private final String HTML_CLASS_NAME_USED_SHOP_OFF_TEXT3 = "usedshop_off_text3";
    private final String HTML_ATTRIBUTE_NAME_HREF = "href";

    private final String url;
    private final RestTemplate restTemplate;

    public AladinBookCrawler(@Value("${bookup.crawler.aladin.url}") String url,
                             RestTemplate restTemplate) {

        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    public Book findByIsbn(String isbn) {
        Element bodyElement = body(restTemplate, createUrl(isbn));

        if (hasNotBook(bodyElement)) return null;

        Elements bookElements = getBookElements(bodyElement);
        return new Book(getTitle(bookElements), getDescription(bookElements), findBookStores(bodyElement));
    }

    private Collection<BookStore> findBookStores(Element element) {
        if (Objects.isNull(element)) return null;

        Elements boxElements = element.getElementsByClass(HTML_CLASS_NAME_SS_BOOK_BOX);

        if (CollectionUtils.isEmpty(boxElements)) return null;

        return getBookStoreElement(boxElements).stream()
                .map(x -> new BookStore("알라딘 : " + x.text(), getBookStoreHref(x)))
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

    private Elements getBookElements(Element element) {
        if (Objects.isNull(element)) return null;

        Elements boxElements = element.getElementsByClass(HTML_CLASS_NAME_SS_BOOK_BOX);

        if (CollectionUtils.isEmpty(boxElements)) return null;

        Elements listElements = boxElements.first().getElementsByClass(HTML_CLASS_NAME_SS_BOOK_LIST);

        if (CollectionUtils.isEmpty(listElements)) return null;

        Elements liElements = listElements.first().getElementsByTag(HTML_TAG_NAME_LI);

        if (CollectionUtils.isEmpty(liElements)) return null;

        return liElements;
    }

    private String getTitle(Elements bookElements) {
        if (Objects.isNull(bookElements)) return null;

        Element bookElement = bookElements.first();

        if (Objects.isNull(bookElement)) return null;

        Elements titleElements = bookElement.getElementsByClass(HTML_CLASS_NAME_BO3);

        if (Objects.isNull(titleElements)) return null;

        Element titleElement = titleElements.first();

        if (Objects.isNull(titleElement)) return null;

        String title = titleElement.text();

        Elements subTitleElements = bookElement.getElementsByClass(HTML_CLASS_NAME_SS_F_G2);

        if (!CollectionUtils.isEmpty(subTitleElements)) {
            title += subTitleElements.first().text();
        }

        return title;
    }

    private String getDescription(Elements elements) {
        if (CollectionUtils.isEmpty(elements)) return null;

        return elements.last().text();
    }

    private Elements getBookStoreElement(Elements elements) {
        Element bookElement = elements.first();
        return bookElement.getElementsByClass(HTML_CLASS_NAME_USED_SHOP_OFF_TEXT3);
    }
}
