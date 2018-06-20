package io.bookup.common.utils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author woniper
 */
public class FutureUtils {

    public static <T> Optional<T> getFutureItem(CompletableFuture<T> future) {
        try {
            return Optional.ofNullable(future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
