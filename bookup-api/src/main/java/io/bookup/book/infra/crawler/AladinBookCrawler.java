package io.bookup.book.infra.crawler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
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

        if(hasNotBook(bodyElement)) return null;

        Elements bookBoxElements = getBookBoxElements(bodyElement);
        Elements bookElements = getBookElements(bookBoxElements);
        return new Book(getTitle(bookElements), getDescription(bookElements), findBookStores(bodyElement));
    }

    private Collection<BookStore> findBookStores(Element bodyElement) {
        Collection<BookStore> bookStores = new HashSet<>();

        Elements bookBoxElements = getBookBoxElements(bodyElement);
        if (Objects.nonNull(bookBoxElements)) {
            for (Element element : getBookStoreElement(bookBoxElements)) {
                bookStores.add(new BookStore("알라딘 : " + element.text(), getBookStoreHref(element)));
            }
        }
        return bookStores;
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

    private Elements getBookBoxElements(Element bodyElement) {
        if (Objects.isNull(bodyElement)) return null;

        return bodyElement.getElementsByClass(HTML_CLASS_NAME_SS_BOOK_BOX);
    }

    private Elements getBookElements(Elements bookBoxElements) {
        if (Objects.isNull(bookBoxElements)) return null;

        Elements bookListElements = bookBoxElements.first().getElementsByClass(HTML_CLASS_NAME_SS_BOOK_LIST);

        if (Objects.isNull(bookListElements)) return null;

        Elements liElements = bookListElements.first().getElementsByTag(HTML_TAG_NAME_LI);

        if (Objects.isNull(liElements)) return null;

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

    private String getDescription(Elements bookElements) {
        if (Objects.isNull(bookElements) || bookElements.isEmpty()) return null;

        Element bookElement = bookElements.last();

        return bookElement.text();
    }

    private Elements getBookStoreElement(Elements bookBoxElements) {
        Element bookElement = bookBoxElements.first();
        return bookElement.getElementsByClass(HTML_CLASS_NAME_USED_SHOP_OFF_TEXT3);
    }
}
