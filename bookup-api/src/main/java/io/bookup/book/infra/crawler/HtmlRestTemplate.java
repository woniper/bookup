package io.bookup.book.infra.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
class HtmlRestTemplate {

    private final RestTemplate restTemplate;

    HtmlRestTemplate() {
        this.restTemplate = new RestTemplate();
    }

    Element getBodyElement(String url) {
        String html = restTemplate.getForObject(url, String.class);
        return Jsoup.parse(html).body();
    }
}
