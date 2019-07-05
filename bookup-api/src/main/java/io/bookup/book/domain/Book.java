package io.bookup.book.domain;

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
    private String image;
    private String author;
    private String price;
    private String isbn;
}
