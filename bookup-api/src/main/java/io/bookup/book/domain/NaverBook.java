package io.bookup.book.domain;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author woniper
 */
@Getter
@NoArgsConstructor
public class NaverBook {

    private int total;

    private int start;

    private int display;

    private List<Item> items;

    @Getter
    @NoArgsConstructor
    public static class Item {
        private String title;
        private String link;
        private String image;
        private String author;
        private String price;
        private String discount;
        private String publisher;
        private String pubdate;
        private String isbn;
        private String description;
    }
}
