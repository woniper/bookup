package io.bookup.library.domain;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author woniper
 */
@Getter
@NoArgsConstructor
public class Library {

    private List<Item> items;

    public Library(List<Item> items) {
        this.items = items;
    }

    @Getter
    public static class Item {
        private String libraryName;
        private String libraryCode;

        public Item(String libraryName, String libraryCode) {
            this.libraryName = libraryName;
            this.libraryCode = libraryCode;
        }
    }
}
