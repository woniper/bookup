package io.bookup.store.infra;

import io.bookup.store.domain.Store;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BookStorePerformanceTest {

    private final String isbn = "9788994492032";

    @Autowired
    private List<StoreRepository> repositories;

    @Test
    public void compare_performance() {
        // given
        StopWatch stopWatch = new StopWatch();

        // when
        stopWatch.start();
        assert_findByIsbnSync();
        stopWatch.stop();
        long syncTime = stopWatch.getTotalTimeMillis();

        stopWatch.start();
        assert_findByIsbnAsync();
        stopWatch.stop();
        long asyncTime = stopWatch.getTotalTimeMillis();

        // then
        assertThat(asyncTime).isGreaterThan(syncTime);
    }

    public void assert_findByIsbnSync() {
        // given
        List<Store> stores = new ArrayList<>();

        // when
        repositories.forEach(x -> stores.addAll(x.findByIsbn(isbn)));

        // then
        assertThat(stores).isNotEmpty();
    }

    public void assert_findByIsbnAsync() {
        // given
        List<Store> stores = new ArrayList<>();

        // when
        repositories.stream()
                .map(x -> CompletableFuture.supplyAsync(() -> x.findByIsbn(isbn)))
                .collect(Collectors.toList())
                .forEach(x -> stores.addAll(x.join()));

        // then
        assertThat(stores).isNotEmpty();
    }
}
