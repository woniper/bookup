package io.bookup.store.app;

import io.bookup.book.app.BookFindAppService;
import io.bookup.book.domain.NotFoundBookException;
import io.bookup.store.domain.BookStore;
import io.bookup.store.domain.Store;
import io.bookup.store.infra.StoreRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static io.bookup.common.utils.FutureUtils.getFutureItem;

/**
 * @author woniper
 */
@Service
public class BookStoreCompositeAppService {

    private final BookFindAppService bookFindAppService;
    private final List<StoreRepository> storeRepositories;

    public BookStoreCompositeAppService(BookFindAppService bookFindAppService,
                                        List<StoreRepository> storeRepositories) {

        this.bookFindAppService = bookFindAppService;
        this.storeRepositories = storeRepositories;
    }

    public BookStore getBook(String isbn) {
        Assert.isTrue(StringUtils.isNumeric(isbn), "잘못된 isbn 입니다.");

        CompletableFuture<BookStore> bookFuture =
                CompletableFuture.supplyAsync(() -> bookFindAppService.getBook(isbn))
                .thenApplyAsync(x -> new BookStore(x, getBookStores(isbn)));

        return getFutureItem(bookFuture)
                .orElseThrow(() -> new NotFoundBookException(isbn));
    }

    private List<Store> getBookStores(String isbn) {
        List<Store> stores = new ArrayList<>();

        storeRepositories.stream()
                .map(x -> CompletableFuture.supplyAsync(() -> x.findByIsbn(isbn)))
                .collect(Collectors.toList())
                .forEach(x -> stores.addAll(getFutureItem(x).orElse(Collections.emptyList())));

        return stores;
    }

}
