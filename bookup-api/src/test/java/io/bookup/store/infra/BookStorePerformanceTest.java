package io.bookup.store.infra;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author woniper
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BookStorePerformanceTest {

    private Date startDate;
    private final String isbn = "9788994492032";

    @Autowired
    private List<StoreRepository> repositories;

    @Before
    public void setUp() throws Exception {
        this.startDate = new Date();
    }

    @After
    public void tearDown() throws Exception {
        log.info("running time : {}", new Date().getTime() - startDate.getTime());
    }

    @Test
    public void findByIsbn_sync() {
        repositories.forEach(x -> x.findByIsbn(isbn));
    }

    @Test
    public void findByIsbn_async() {
        repositories.stream()
                .map(x -> CompletableFuture.supplyAsync(() -> x.findByIsbn(isbn)))
                .collect(Collectors.toList());
    }
}
