package io.bookup.book.infra.rest;

import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

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

    MultiValueMap<String, String> getHeaderMap() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("X-Naver-Client-Id", Collections.singletonList(getClientId()));
        headers.put("X-Naver-Client-Secret", Collections.singletonList(getClientSecret()));

        return headers;
    }

    String createUrl(String query) {
        return String.format(getUrl(), query);
    }

    String createUrl(String query, long page, int offset) {
        return String.format("%s&start=%d&display=%d", createUrl(query), page + 1, offset);
    }
}
