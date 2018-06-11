package io.bookup.book.infra.crawler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

    private final String HTML_CLASS_NAME_TITLE = "title";
    private final String HTML_CLASS_NAME_WRITER = "writer";
    private final String HTML_CLASS_NAME_BK_BOOK = "bk_book";
    private final String HTML_CLASS_NAME_BOOK_MOD = "book_mod";
    private final String HTML_CLASS_NAME_KY_BOOK_LIST = "ky_book_list";
    private final String HTML_CLASS_NAME_BK_STOCK = "bk_stock";
    private final String HTML_CLASS_NAME_TOTAL = "total";

    private final String url;
    private final RestTemplate restTemplate;
    private final KyoboProperties properties;

    public KyoboBookCrawler(@Value("${bookup.crawler.kyobo.url}") String url,
                            RestTemplate restTemplate,
                            KyoboProperties properties) {

        this.url = url;
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public Book findByIsbn(String isbn) {
        Collection<KyoboBookStoreRequestCommand> bookStores = getBookStoreRequestCommandList(isbn);

        if (hasNotBook(bookStores)) return null;

        Optional<KyoboBookStoreRequestCommand> requestCommand = bookStores.stream().findFirst();

        if (!requestCommand.isPresent()) {
            return null;
        }

        Elements bookElements = getBookElements(requestCommand.get().getBodyElement());
        Elements bookModElements = getBookModElements(bookElements);
        Element bookElement = getBookElement(bookModElements);
        return new Book(getTitle(bookElement), getDescription(bookElement), findBookStores(bookStores));
    }

    private String getTitle(Element element) {
        return element.getElementsByClass(HTML_CLASS_NAME_TITLE).first().text();
    }

    private String getDescription(Element element) {
        return element.getElementsByClass(HTML_CLASS_NAME_WRITER).first().text();
    }

    private Collection<BookStore> findBookStores(Collection<KyoboBookStoreRequestCommand> bookStores) {
        if (hasNotBook(bookStores)) return BookStore.EMPTY;

        Collection<BookStore> findBookStores = new HashSet<>();

        for (KyoboBookStoreRequestCommand bookStore : bookStores) {
            Element bodyElement = bookStore.getBodyElement();
            Elements bookElements = getBookElements(bodyElement);
            Elements bookModElements = getBookModElements(bookElements);
            Element bookStockElement = getBookStockElement(bookModElements);
            Element totalElement = getTotalElement(bookStockElement);

            if (isRetainBook(totalElement)) {
                findBookStores.add(new BookStore(bookStore.getStoreName(), bookStore.getUrl()));
            }
        }

        return findBookStores;
    }

    private boolean hasNotBook(Collection<KyoboBookStoreRequestCommand> bookStores) {
        return bookStores.stream()
                .noneMatch(element -> element.getBodyElement().getAllElements().hasClass(HTML_CLASS_NAME_KY_BOOK_LIST));
    }

    private boolean isRetainBook(Element element) {
        Assert.notNull(element, "not found kyobo retainBook element");
        return element.text().matches(".*\\d.*");
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
        Assert.notEmpty(bookModElements, "not found kyobo BookModElements");
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
                    return new KyoboBookStoreRequestCommand(
                            x.getStoreName(),
                            url,
                            CompletableFuture.supplyAsync(() -> body(restTemplate, url))
                    );
                })
                .collect(Collectors.toList());
    }

    private String createUrl(String storeId, String isbn) {
        return String.format(this.url, storeId, isbn);
    }

    @Getter
    private class KyoboBookStoreRequestCommand {

        private final String storeName;
        private final String url;
        private final CompletableFuture<Element> bodyElement;

        KyoboBookStoreRequestCommand(String storeName, String url, CompletableFuture<Element> bodyElement) {
            this.storeName = storeName;
            this.url = url;
            this.bodyElement = bodyElement;
        }

        Element getBodyElement() {
            try {
                return bodyElement.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
