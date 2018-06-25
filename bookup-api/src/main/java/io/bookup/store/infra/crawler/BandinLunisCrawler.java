package io.bookup.store.infra.crawler;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.bookup.store.domain.Store;
import io.bookup.store.infra.StoreRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class BandinLunisCrawler implements StoreRepository {

    private final String HTML_CLASS_NAME_BOOK_MT3 = "mt3";
    private final String HTML_TAG_NAME_TBODY = "tbody";
    private final String HTML_TAG_NAME_TR = "tr";
    private final String HTML_TAG_NAME_TH = "th";
    private final String HTML_TAG_NAME_TD = "td";
    private final String HTML_TAG_NAME_A = "a";
    private final String HTML_ATTR_NAME_HREF = "href";

    private final String url;
    private final String storeUrl;
    private final String hrefUrl;
    private final RestTemplate restTemplate;

    public BandinLunisCrawler(@Value("${bookup.crawler.bandi.url}") String url,
                              @Value("${bookup.crawler.bandi.storeUrl}") String storeUrl,
                              @Value("${bookup.crawler.bandi.hrefUrl}") String hrefUrl,
                              RestTemplate restTemplate) {

        this.url = url;
        this.storeUrl = storeUrl;
        this.hrefUrl = hrefUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Store> findByIsbn(String isbn) {
        String productId = response(isbn)
                .map(BandinLunisBook::getItems)
                .orElse(Collections.emptyList()).stream()
                .findFirst()
                .map(BandinLunisBook.Item::getProductId)
                .orElse(null);

        if (!StringUtils.hasText(productId)) return Collections.emptyList();

        Element tbodyElement = getTBodyElement(productId);

        return findBookStores(productId, tbodyElement);
    }

    private List<Store> findBookStores(String productId, Element tbodyElement) {
        if (Objects.isNull(tbodyElement)) return Collections.emptyList();

        List<Store> stores = new ArrayList<>();
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
                            stores.add(new Store(
                                    String.format("반디앤루니스 : %s", storeName),
                                    String.format(hrefUrl, productId, storeId)));
                        }
                    }
                }
            }
        }

        return stores;
    }

    private Element getTBodyElement(String productId) {
        Element bodyElement = getBodyElement(String.format(storeUrl, productId));

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

    private Optional<BandinLunisBook> response(String isbn) {
        ResponseEntity<BandinLunisBook> responseEntity =
                restTemplate.getForEntity(String.format(url, isbn), BandinLunisBook.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            return Optional.of(responseEntity.getBody());
        }

        return Optional.empty();
    }

    @Getter
    @NoArgsConstructor
    static class BandinLunisBook {

        @JsonProperty("result")
        private List<Item> items;

        @Getter
        @NoArgsConstructor
        static class Item {
            @JsonProperty("prod_id")
            private String productId;

            @JsonProperty("prod_name")
            private String title;

            @JsonProperty("contents_description")
            private String description;

            @JsonProperty("barcode")
            private String isbn;
        }
    }
}
