package io.bookup.common.utils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * @author woniper
 */
@Slf4j
public class FutureUtils {

    public static <T> Optional<T> getFutureItem(CompletableFuture<T> future) {
        Assert.notNull(future, "not null CompletableFuture");

        try {
            return Optional.ofNullable(future.get());
        } catch (Exception e) {
            log.error("error get future item", e);
        }

        return Optional.empty();
    }
}
