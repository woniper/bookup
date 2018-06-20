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
public class BandinLunisBook {

    @JsonProperty("result")
    private List<Item> items;

    @Getter
    @NoArgsConstructor
    public static class Item {
        @JsonProperty("prod_id")
        private String productId;

        @JsonProperty("prod_name")
        private String title;

        @JsonProperty("contents_description")
        private String description;

        @JsonProperty("barcode")
        private String isbn;
    }
}
