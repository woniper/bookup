package io.bookup.book.domain;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author woniper
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Book {

    private String title;
    private String description;
    private String link;
    private String image;
    private String author;
    private String price;
    private String publisher;
    private String publishDate;
    private String isbn;

    public String getIsbn() {
        if (Objects.nonNull(isbn)) {
            String[] split = isbn.split(" ");
            return split.length == 2 ? split[1] : split[0];
        }

        return null;
    }
}
