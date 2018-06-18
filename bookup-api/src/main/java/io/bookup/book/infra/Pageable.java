package io.bookup.book.infra;

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
}
