package io.bookup.book.infra.crawler;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author woniper
 */
@Data
@Component
@ConfigurationProperties(prefix = "bookup.crawler.kyobo")
class KyoboProperties {

    private String url;
    private List<StoreList> storeList;

    @Data
    @NoArgsConstructor
    static class StoreList {
        private String storeName;
        private String storeId;

        StoreList(String storeName, String storeId) {
            this.storeName = storeName;
            this.storeId = storeId;
        }
    }
}
