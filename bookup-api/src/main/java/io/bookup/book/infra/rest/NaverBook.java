package io.bookup.book.infra.rest;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author woniper
 */
@Getter
@NoArgsConstructor
class NaverBook {

    private List<Item> items;

    @Getter
    @NoArgsConstructor
    static class Item {
        private String title;
        private String link;
        private String image;
        private String author;
        private String price;
        private String discount;
        private String publisher;
        @Getter(AccessLevel.PRIVATE)
        private String pubdate;
        private String isbn;
        private String description;

        String getPublishDate() {
            return getPubdate();
        }
    }
}
