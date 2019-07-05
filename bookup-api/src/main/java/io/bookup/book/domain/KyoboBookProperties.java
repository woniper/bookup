package io.bookup.book.domain;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author woniper
 */
@Setter
@Component
@ConfigurationProperties(prefix = "bookup.crawler.kyobo")
public class KyoboBookProperties {

    private String listUrl;
    private String isbnUrl;


    String getListUrl(String keyword, int page, int offset) {
        int startNo = page == 0 ? 0 : page * offset;

        return String.format(this.listUrl, keyword, startNo);
    }

    String getIsbnUrl(String isbn) {
        return String.format(this.isbnUrl, isbn);
    }
}
