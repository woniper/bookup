package io.bookup.book.api.representation;

import io.bookup.book.infra.rest.NaverBook;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author woniper
 */
@Getter
@NoArgsConstructor
public class NaverBookResponseDto {

    private Pageable pageable;

    private List<Item> items;

    public NaverBookResponseDto(NaverBook naverBook) {
        this.pageable = new Pageable(naverBook.getTotal(), naverBook.getStart(), naverBook.getDisplay());
        this.items = naverBook.getItems().stream()
                .map(Item::new)
                .collect(Collectors.toList());
    }

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
        private String publishDate;
        private String isbn10;
        private String isbn13;
        private String description;

        public Item(NaverBook.Item source) {
            this.title = source.getTitle();
            this.link = source.getLink();
            this.image = source.getImage();
            this.author = source.getAuthor();
            this.price = source.getPrice();
            this.discount = source.getDiscount();
            this.publisher = source.getPublisher();
            this.publishDate = source.getPubdate();
            String isbn = source.getIsbn();
            if (Objects.nonNull(isbn)) {
                String[] split = isbn.split(" ");
                this.isbn10 = split[0];
                if (split.length >= 2) {
                    this.isbn13 = split[1];
                }
            }

            this.description = source.getDescription();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Pageable {
        private int total;
        private int page;
        private int size;

        Pageable(int total, int page, int size) {
            this.total = total;
            this.page = page;
            this.size = size;
        }
    }
}
