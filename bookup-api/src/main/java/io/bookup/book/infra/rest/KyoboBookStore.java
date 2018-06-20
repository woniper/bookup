package io.bookup.book.infra.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author woniper
 */
@Getter
@NoArgsConstructor
public class KyoboBookStore {

    private List<Item> items;

    public KyoboBookStore(List<Item> items) {
        this.items = items;
    }

    @NoArgsConstructor
    public static class Item {

        @JsonProperty("site")
        private String site;

        @JsonProperty("qty")
        private int qty;

        @JsonProperty("code_desc")
        private String codeDesc;

        public Item(String site, int qty, String codeDesc) {
            this.site = site;
            this.qty = qty;
            this.codeDesc = codeDesc;
        }

        public String getStoreId() {
            return this.site;
        }

        public int getAmount() {
            return this.qty;
        }

        public String getStoreName() {
            return String.format("교보문고 : %s", this.codeDesc);
        }
    }
}
