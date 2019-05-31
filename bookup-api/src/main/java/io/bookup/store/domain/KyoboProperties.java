package io.bookup.store.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author woniper
 */
@Data
@Component
@ConfigurationProperties(prefix = "bookup.rest.kyobo")
public class KyoboProperties {

    private String url;
    private String storeUrl;

    String createUrl(String isbn) {
        return String.format(url, isbn);
    }

    String createUrl(String storeId, String storeName) {
        return String.format(storeUrl, storeId, storeName);
    }
}
