package io.bookup.book.infra.crawler;

import io.bookup.book.domain.BookStore;
import io.bookup.book.infra.BookFinder;
import io.bookup.book.infra.rest.BandinLunisBook;
import io.bookup.book.infra.rest.BandinLunisRestTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class BandinLunisBookCrawler implements BookFinder<List<BookStore>> {

    private final String HTML_CLASS_NAME_BOOK_MT3 = "mt3";
    private final String HTML_TAG_NAME_TBODY = "tbody";
    private final String HTML_TAG_NAME_TR = "tr";
    private final String HTML_TAG_NAME_TH = "th";
    private final String HTML_TAG_NAME_TD = "td";
    private final String HTML_TAG_NAME_A = "a";
    private final String HTML_ATTR_NAME_HREF = "href";

    private final String url;
    private final String hrefUrl;
    private final RestTemplate restTemplate;
    private final BandinLunisRestTemplate bandinLunisRestTemplate;

    public BandinLunisBookCrawler(@Value("${bookup.crawler.bandi.storeUrl}") String url,
                                  @Value("${bookup.crawler.bandi.hrefUrl}") String hrefUrl,
                                  RestTemplate restTemplate,
                                  BandinLunisRestTemplate bandinLunisRestTemplate) {

        this.url = url;
        this.hrefUrl = hrefUrl;
        this.restTemplate = restTemplate;
        this.bandinLunisRestTemplate = bandinLunisRestTemplate;
    }

    @Override
    public List<BookStore> findByIsbn(String isbn) {
        String productId = bandinLunisRestTemplate.findByIsbn(isbn)
                .map(BandinLunisBook::getItems)
                .orElse(Collections.emptyList()).stream()
                .findFirst()
                .map(BandinLunisBook.Item::getProductId)
                .orElse(null);

        if (!StringUtils.hasText(productId)) return Collections.emptyList();

        Element tbodyElement = getTBodyElement(productId);

        return findBookStores(productId, tbodyElement);
    }

    private List<BookStore> findBookStores(String productId, Element tbodyElement) {
        if (Objects.isNull(tbodyElement)) return Collections.emptyList();

        List<BookStore> bookStores = new ArrayList<>();
        Elements trs = tbodyElement.getElementsByTag(HTML_TAG_NAME_TR);

        for (int trIndex = 0; trIndex < trs.size() - 1; trIndex++) {
            Elements ths = trs.get(trIndex).getElementsByTag(HTML_TAG_NAME_TH);
            Elements tds = trs.get(trIndex + 1).getElementsByTag(HTML_TAG_NAME_TD);

            if (ths.size() == tds.size()) {
                for (int thIndex = 0; thIndex < ths.size(); thIndex++) {
                    String storeName = ths.get(thIndex).text().trim();
                    Elements aElements = tds.get(thIndex).getElementsByTag(HTML_TAG_NAME_A);

                    if (!CollectionUtils.isEmpty(aElements)) {
                        int amount = Integer.parseInt(aElements.first().text().trim());
                        String hrefAttribute = aElements.attr(HTML_ATTR_NAME_HREF);
                        String storeId = hrefAttribute.substring(
                                hrefAttribute.indexOf("'"),
                                hrefAttribute.lastIndexOf("'"))
                                .replaceAll("'", "");

                        if (StringUtils.hasText(storeName) && amount > 0) {
                            bookStores.add(new BookStore(
                                    String.format("반디앤루니스 : %s", storeName),
                                    String.format(hrefUrl, productId, storeId)));
                        }
                    }

                }
            }
        }

        return bookStores;
    }

    private Element getTBodyElement(String productId) {
        Element bodyElement = getBodyElement(String.format(url, productId));

        if (hasNotBook(bodyElement)) return null;

        Elements tbodyElements = bodyElement.getElementsByTag(HTML_TAG_NAME_TBODY);

        if (CollectionUtils.isEmpty(tbodyElements)) return null;

        return tbodyElements.first();
    }

    private boolean hasNotBook(Element element) {
        if (Objects.isNull(element)) return false;

        return !element.getAllElements().hasClass(HTML_CLASS_NAME_BOOK_MT3);
    }

    private Element getBodyElement(String url) {
        String html = restTemplate.getForObject(url, String.class);
        return Jsoup.parse(html).body();
    }
}
