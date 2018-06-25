package io.bookup.store.infra;

import io.bookup.store.domain.Store;
import java.util.List;

/**
 * @author woniper
 */
public interface StoreRepository {

    List<Store> findByIsbn(String isbn);

}