package io.bookup.store.infra.crawler;

import java.util.Collection;
import java.util.HashSet;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class AladinBookCrawler implements BookCrawler {

    @Value("${bookup.crawler.aladin.url}")
    private String url;

    private final static String HTML_CLASS_NAME_SS_BOOK_BOX = "ss_book_box";
    private final static String HTML_CLASS_NAME_SS_BOOK_LIST = "ss_book_list";
    private final static String HTML_TAG_NAME_LI = "li";
    private final static String HTML_CLASS_NAME_BO3 = "bo3";
    private final static String HTML_CLASS_NAME_SS_F_G2 = "ss_f_g2";
    private final static String HTML_CLASS_NAME_USED_SHOP_OFF_TEXT3 = "usedshop_off_text3";
    private final static String HTML_ATTRIBUTE_NAME_HREF = "href";

    private final RestTemplate restTemplate;

    public AladinBookCrawler(RestTemplate restTemplate) {
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
        for (Element element : getBookStoreElement(bookBoxElements)) {
            bookStores.add(new BookStore("알라딘 : " + element.text(), getBookStoreHref(element)));
        }
        return bookStores;
    }

    private boolean hasNotBook(Element bodyElement) {
        return !bodyElement.getAllElements().hasClass(HTML_CLASS_NAME_SS_BOOK_BOX);
    }

    private String createUrl(String isbn) {
        return String.format(this.url, isbn);
    }

    private String getBookStoreHref(Element element) {
        return element.attr(HTML_ATTRIBUTE_NAME_HREF);
    }

    private Elements getBookBoxElements(Element bodyElement) {
        return bodyElement.getElementsByClass(HTML_CLASS_NAME_SS_BOOK_BOX);
    }

    private Elements getBookElements(Elements bookBoxElements) {
        Assert.notEmpty(bookBoxElements, "not found aladin BookBoxElements");

        Elements bookListElements = bookBoxElements.first().getElementsByClass(HTML_CLASS_NAME_SS_BOOK_LIST);
        Assert.notEmpty(bookListElements, "not found aladin BookListElements");

        Elements liElements = bookListElements.first().getElementsByTag(HTML_TAG_NAME_LI);
        Assert.notEmpty(bookListElements, "not found aladin LiElements");

        return liElements;
    }

    private String getTitle(Elements bookElements) {
        Element bookElement = bookElements.first();
        Elements titleElements = bookElement.getElementsByClass(HTML_CLASS_NAME_BO3);
        Assert.notNull(bookElement, "not found aladin TitleElements");
        String title = titleElements.first().text();

        Elements subTitleElements = bookElement.getElementsByClass(HTML_CLASS_NAME_SS_F_G2);
        if (!CollectionUtils.isEmpty(subTitleElements)) {
            title += subTitleElements.first().text();
        }

        return title;
    }

    private String getDescription(Elements bookElements) {
        Element bookElement = bookElements.last();
        Assert.notNull(bookElement, "not found aladin BookDescriptionElement");

        return bookElement.text();
    }

    private Elements getBookStoreElement(Elements bookBoxElements) {
        Element bookElement = bookBoxElements.first();
        Elements bookStoreElements = bookElement.getElementsByClass(HTML_CLASS_NAME_USED_SHOP_OFF_TEXT3);
        Assert.notEmpty(bookStoreElements, "not found aladin BookStoreElements");
        return bookStoreElements;
    }
}
