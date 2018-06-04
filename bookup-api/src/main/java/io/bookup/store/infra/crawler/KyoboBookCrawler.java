package io.bookup.store.infra.crawler;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Slf4j
@Component
public class KyoboBookCrawler implements BookCrawler {

    @Value("${bookup.crawler.kyobo.url}")
    private String url;

    private final static String HTML_CLASS_NAME_TITLE = "title";
    private final static String HTML_CLASS_NAME_WRITER = "writer";
    private final static String HTML_CLASS_NAME_BK_BOOK = "bk_book";
    private final static String HTML_CLASS_NAME_BOOK_MOD = "book_mod";
    private final static String HTML_CLASS_NAME_KY_BOOK_LIST = "ky_book_list";
    private final static String HTML_CLASS_NAME_BK_STOCK = "bk_stock";
    private final static String HTML_CLASS_NAME_TOTAL = "total";

    private final RestTemplate restTemplate;
    private final KyoboProperties properties;

    public KyoboBookCrawler(RestTemplate restTemplate, KyoboProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public Book findByIsbn(String isbn) {
        Collection<KyoboBookStoreRequestCommand> bookStores = getBookStoreRequestCommandList(isbn);

        if(hasNotBook(bookStores)) return null;

        try {
            Element body = bookStores.stream().findFirst().orElseThrow(() -> new IllegalArgumentException("empty kyobo html body")).getBodyElement();
            Elements bookElements = getBookElements(body);
            Elements bookModElements = getBookModElements(bookElements);
            Element bookElement = getBookElement(bookModElements);

            String title = bookElement.getElementsByClass(HTML_CLASS_NAME_TITLE).first().text();
            String description = bookElement.getElementsByClass(HTML_CLASS_NAME_WRITER).first().text();

            return new Book(title, description, findBookStores(bookStores));
        } catch (IllegalArgumentException ex) {
            log.error("not found kyobo book");
            return null;
        }
    }

    private Collection<BookStore> findBookStores(Collection<KyoboBookStoreRequestCommand> bookStores) {
        if(hasNotBook(bookStores)) return BookStore.EMPTY_LIST;

        Collection<BookStore> findBookStores = new HashSet<>();

        try {
            for (KyoboBookStoreRequestCommand bookStore : bookStores) {
                Element bodyElement = bookStore.getBodyElement();
                Elements bookElements = getBookElements(bodyElement);
                Elements bookModElements = getBookModElements(bookElements);
                Element bookStockElement = getBookStockElement(bookModElements);
                Element totalElement = getTotalElement(bookStockElement);

                if (isDigit(totalElement.text())) {
                    findBookStores.add(new BookStore(bookStore.getStoreName(), bookStore.getUrl()));
                }
            }
        } catch (IllegalArgumentException ex) {
            log.error("not found kyobo book");
            return BookStore.EMPTY_LIST;
        }

        return findBookStores;
    }

    private boolean hasNotBook(Collection<KyoboBookStoreRequestCommand> bookStores) {
        return bookStores.stream()
                .noneMatch(element -> element.getBodyElement().getAllElements().hasClass(HTML_CLASS_NAME_KY_BOOK_LIST));
    }

    private boolean isDigit(String text) {
        return text.matches(".*\\d.*");
    }

    private Element getTotalElement(Element bookStockElement) {
        return bookStockElement.getElementsByClass(HTML_CLASS_NAME_TOTAL).first();
    }

    private Element getBookStockElement(Elements bookModElements) {
        Elements bookStockElements = bookModElements.first().getElementsByClass(HTML_CLASS_NAME_BK_STOCK);
        Assert.notNull(bookStockElements, "not found kyobo BookStockElement");
        return bookStockElements.first();
    }

    private Element getBookElement(Elements bookModElements) {
        Element bookElement = bookModElements.first().getElementsByClass(HTML_CLASS_NAME_BK_BOOK).first();
        Assert.notNull(bookElement, "not found kyobo BookElement");
        return bookElement;
    }

    private Elements getBookModElements(Elements bookElements) {
        Elements bookModElements = bookElements.first().getElementsByClass(HTML_CLASS_NAME_BOOK_MOD);
        Assert.notEmpty(bookElements, "not found kyobo BookModElements");
        return bookModElements;
    }

    private Elements getBookElements(Element body) {
        Elements bookElements = body.getElementsByClass(HTML_CLASS_NAME_KY_BOOK_LIST);
        Assert.notEmpty(bookElements, "not found kyobo BookElements");
        return bookElements;
    }

    private Collection<KyoboBookStoreRequestCommand> getBookStoreRequestCommandList(String isbn) {
        return properties.getStoreDataList().stream()
                .map(x -> {
                    String url = createUrl(x.getStoreId(), isbn);
                    return new KyoboBookStoreRequestCommand(x.getStoreName(), url, body(restTemplate, url));
                })
                .collect(Collectors.toList());
    }

    private String createUrl(int storeId, String isbn) {
        return String.format(this.url, storeId, isbn);
    }

    @Getter
    private class KyoboBookStoreRequestCommand {

        private final String storeName;
        private final String url;
        private final Element bodyElement;

        KyoboBookStoreRequestCommand(String storeName, String url, Element bodyElement) {
            this.storeName = storeName;
            this.url = url;
            this.bodyElement = bodyElement;
        }
    }
}
