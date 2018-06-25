package io.bookup.store.infra.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.bookup.store.domain.Store;
import io.bookup.store.infra.StoreRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class KyoboRestTemplate implements StoreRepository {

    private final KyoboProperties properties;
    private final RestTemplate restTemplate;

    public KyoboRestTemplate(KyoboProperties properties,
                             RestTemplate restTemplate) {

        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Store> findByIsbn(String isbn) {
        KyoboBookStore kyoboBookStore = response(isbn);
        return mapToBookStore(isbn, kyoboBookStore);
    }

    private KyoboBookStore response(String isbn) {
        ResponseEntity<KyoboBookStore> responseEntity = restTemplate.postForEntity(
                properties.createUrl(isbn),
                StringUtils.EMPTY,
                KyoboBookStore.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            return responseEntity.getBody();
        }

        return null;
    }

    private List<Store> mapToBookStore(String isbn, KyoboBookStore kyoboBookStore) {
        if (StringUtils.isEmpty(isbn) || Objects.isNull(kyoboBookStore)) return Collections.emptyList();

        return kyoboBookStore.getItems().stream()
                .filter(x -> x.getAmount() > 0)
                .map(x -> new Store(x.getStoreName(), properties.createUrl(x.getStoreId(), isbn)))
                .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    static class KyoboBookStore {

        private List<Item> items;

        @NoArgsConstructor
        static class Item {

            @JsonProperty("site")
            private String site;

            @JsonProperty("qty")
            private int qty;

            @JsonProperty("code_desc")
            private String codeDesc;

            String getStoreId() {
                return this.site;
            }

            int getAmount() {
                return this.qty;
            }

            String getStoreName() {
                return String.format("교보문고 : %s", this.codeDesc);
            }
        }
    }
}
