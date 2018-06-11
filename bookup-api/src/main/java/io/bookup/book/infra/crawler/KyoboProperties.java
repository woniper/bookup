package io.bookup.book.infra.crawler;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
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
    private List<StoreDataList> storeDataList;

    @Data
    @AllArgsConstructor
    static class StoreDataList {
        private String storeName;
        private String storeId;
    }
}
