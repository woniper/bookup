package io.bookup.library.infra;

import io.bookup.library.domain.Library;

/**
 * @author woniper
 */
public interface LibraryRepository {

    Library findByIsbn(String isbn);
}
