package io.bookup.book.infra.crawler;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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

    private final RestTemplate restTemplate;
    private final KyoboProperties properties;

    public KyoboBookCrawler(RestTemplate restTemplate,
                            KyoboProperties properties) {

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

        Element bkElement = getBkElement(requestCommand.get().getBodyElement());

        return new Book(getTitle(bkElement), getDescription(bkElement), findBookStores(bookStores));
    }

    private String getTitle(Element element) {
        if (Objects.isNull(element)) return null;

        return element.getElementsByClass(HTML_CLASS_NAME_TITLE).first().text();
    }

    private String getDescription(Element element) {
        if (Objects.isNull(element)) return null;

        return element.getElementsByClass(HTML_CLASS_NAME_WRITER).first().text();
    }

    private Collection<BookStore> findBookStores(Collection<KyoboBookStoreRequestCommand> bookStores) {
        if (hasNotBook(bookStores)) return BookStore.EMPTY;

        return bookStores.stream()
                .filter(x -> isRetainBook(getTotalElement(x)))
                .map(x -> new BookStore(x.getStoreName(), x.getUrl()))
                .collect(Collectors.toList());
    }

    private Element getTotalElement(KyoboBookStoreRequestCommand requestCommand) {
        Element bodyElement = requestCommand.getBodyElement();

        if (Objects.isNull(bodyElement)) return null;

        Elements listElements = getListElements(bodyElement);

        if (CollectionUtils.isEmpty(listElements)) return null;

        Elements modElements = getModElements(listElements);

        if (CollectionUtils.isEmpty(modElements)) return null;

        Elements stockElements = modElements.first().getElementsByClass(HTML_CLASS_NAME_BK_STOCK);

        if (CollectionUtils.isEmpty(stockElements)) return null;

        Element stockElement = stockElements.first();

        if (Objects.isNull(stockElement)) return null;

        Elements totalElements = stockElement.getElementsByClass(HTML_CLASS_NAME_TOTAL);

        if (Objects.isNull(totalElements)) return null;

        return totalElements.first();
    }

    private boolean hasNotBook(Collection<KyoboBookStoreRequestCommand> bookStores) {
        if (Objects.isNull(bookStores) || bookStores.isEmpty()) return false;

        return bookStores.stream()
                .noneMatch(element -> element.getBodyElement().getAllElements().hasClass(HTML_CLASS_NAME_KY_BOOK_LIST));
    }

    private boolean isRetainBook(Element element) {
        if (Objects.isNull(element)) return false;

        return element.text().matches(".*\\d.*");
    }

    private Element getBkElement(Element element) {
        if (Objects.isNull(element)) return null;

        Elements listElements = getListElements(element);

        if (CollectionUtils.isEmpty(listElements)) return null;

        Elements modElements = getModElements(listElements);

        if (CollectionUtils.isEmpty(modElements)) return null;

        Elements bkElements = modElements.first().getElementsByClass(HTML_CLASS_NAME_BK_BOOK);

        if (CollectionUtils.isEmpty(bkElements)) return null;

        return bkElements.first();
    }

    private Elements getModElements(Elements elements) {
        if (CollectionUtils.isEmpty(elements)) return null;

        return elements.first().getElementsByClass(HTML_CLASS_NAME_BOOK_MOD);
    }

    private Elements getListElements(Element element) {
        if (Objects.isNull(element)) return null;

        return element.getElementsByClass(HTML_CLASS_NAME_KY_BOOK_LIST);
    }

    private Collection<KyoboBookStoreRequestCommand> getBookStoreRequestCommandList(String isbn) {
        return properties.getStoreList().stream()
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
        return String.format(properties.getUrl(), storeId, isbn);
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
