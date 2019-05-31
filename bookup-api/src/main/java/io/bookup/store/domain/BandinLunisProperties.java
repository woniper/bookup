package io.bookup.store.domain;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author woniper
 */
@Data
@Component
@ConfigurationProperties(prefix = "bookup.crawler.bandi")
public class BandinLunisProperties {

    private String apiUrl;
    private String storeUrl;
    private String hrefUrl;

    String createApiUrl(String isbn) {
        return String.format(this.apiUrl, isbn);
    }

    String createStoreUrl(String productId) {
        return String.format(this.storeUrl, productId);
    }

    String createHrefUrl(String productId, String storeId) {
        return String.format(this.hrefUrl, productId, storeId);
    }

}
