package io.bookup.book.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author woniper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "bookup.rest.naver")
public class NaverBookProperties {

    private String url;
    private String clientId;
    private String clientSecret;

    String createUrl(String query) {
        return String.format(getUrl(), query);
    }

    String createUrl(String query, long page, int offset) {
        return String.format("%s&start=%d&display=%d", createUrl(query), page + 1, offset);
    }
}
