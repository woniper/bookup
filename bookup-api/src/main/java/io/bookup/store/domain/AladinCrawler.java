package io.bookup.store.domain;

import io.bookup.store.domain.Store;
import io.bookup.store.infra.StoreRepository;
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
public class AladinCrawler implements StoreRepository {

    private final String HTML_CLASS_NAME_SS_BOOK_BOX = "ss_book_box";
    private final String HTML_CLASS_NAME_USED_SHOP_OFF_TEXT3 = "usedshop_off_text3";
    private final String HTML_ATTRIBUTE_NAME_HREF = "href";

    private final String url;
    private final RestTemplate restTemplate;

    AladinCrawler(@Value("${bookup.crawler.aladin.url}") String url,
                  RestTemplate restTemplate) {

        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Store> findByIsbn(String isbn) {
        return findBookStores(getBodyElement(String.format(this.url, isbn)));
    }

    private List<Store> findBookStores(Element element) {
        if (Objects.isNull(element) || hasNotBook(element)) return Collections.emptyList();

        Elements boxElements = element.getElementsByClass(HTML_CLASS_NAME_SS_BOOK_BOX);

        if (CollectionUtils.isEmpty(boxElements)) return Collections.emptyList();

        return boxElements.first().getElementsByClass(HTML_CLASS_NAME_USED_SHOP_OFF_TEXT3).stream()
                .map(x -> new Store(
                        "알라딘 : " + x.text(),
                        x.attr(HTML_ATTRIBUTE_NAME_HREF)))
                .collect(Collectors.toList());
    }

    private boolean hasNotBook(Element bodyElement) {
        if (Objects.isNull(bodyElement)) return false;

        return !bodyElement.getAllElements().hasClass(HTML_CLASS_NAME_SS_BOOK_BOX);
    }

    private Element getBodyElement(String url) {
        String html = restTemplate.getForObject(url, String.class);
        return Jsoup.parse(html).body();
    }
}
