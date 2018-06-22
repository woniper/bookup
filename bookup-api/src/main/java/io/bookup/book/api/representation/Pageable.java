package io.bookup.book.api.representation;

import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author woniper
 */
@Data
@NoArgsConstructor
public class Pageable {

    private int page = 0;

    @Size(max = 100)
    private int size = 20;

    @Data
    @NoArgsConstructor
    public static class Response extends Pageable {
        private int total;

        public Response(int page, int size, int total) {
            setPage(page);
            setSize(size);
            setTotal(total);
        }
    }
}
