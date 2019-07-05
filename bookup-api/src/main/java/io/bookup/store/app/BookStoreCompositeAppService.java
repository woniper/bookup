package io.bookup.store.app;

import io.bookup.book.app.FindBookAppService;
import io.bookup.store.domain.BookStore;
import io.bookup.store.domain.Store;
import io.bookup.store.domain.Store.StoreType;
import io.bookup.store.infra.StoreRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @author woniper
 */
@Service
public class BookStoreCompositeAppService {

    private final FindBookAppService findBookAppService;
    private final List<StoreRepository> storeRepositories;

    public BookStoreCompositeAppService(FindBookAppService findBookAppService,
                                        List<StoreRepository> storeRepositories) {

        this.findBookAppService = findBookAppService;
        this.storeRepositories = storeRepositories;
    }

    public BookStore getBook(String isbn) {
        return getBook(isbn, StoreType.getTypes());
    }

    @Cacheable(value = "bookStore", key = "#isbn + #storeTypes")
    public BookStore getBook(String isbn, Set<StoreType> storeTypes) {
        Assert.isTrue(StringUtils.isNumeric(isbn), "잘못된 isbn 입니다.");

        CompletableFuture<BookStore> bookFuture =
                CompletableFuture.supplyAsync(() -> findBookAppService.getBook(isbn))
                        .thenApplyAsync(x -> {
                            Set<StoreType> types = CollectionUtils.isEmpty(storeTypes) ? StoreType.getTypes() : storeTypes;
                            return new BookStore(x, getBookStores(isbn, types));
                        });

        return bookFuture.join();
    }

    private List<Store> getBookStores(String isbn, Set<StoreType> storeTypes) {
        List<Store> stores = new ArrayList<>();

        storeRepositories.stream()
                .filter(x -> storeTypes.stream().anyMatch(type -> type.typeEquals(x.getClass())))
                .map(x -> CompletableFuture.supplyAsync(() -> x.findByIsbn(isbn)))
                .collect(Collectors.toList())
                .forEach(x -> stores.addAll(x.join()));

        return stores;
    }
}
