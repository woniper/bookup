package io.bookup.store.infra.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
public interface BookCrawler {

    Book findByIsbn(String isbn);

    default Element body(RestTemplate restTemplate, String url) {
        String html = restTemplate.getForObject(url, String.class);
        return Jsoup.parse(html).body();
    }
}