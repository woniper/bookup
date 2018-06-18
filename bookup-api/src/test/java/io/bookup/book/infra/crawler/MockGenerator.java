package io.bookup.book.infra.crawler;

import io.bookup.book.domain.Book;
import io.bookup.book.domain.BookStore;
import io.bookup.book.infra.rest.KyoboBookRestTemplate;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author woniper
 */
class MockGenerator {

    static RestTemplate mockRestTemplate(String filePath, Charset charset) throws IOException {
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(LoadFileUtil.load(filePath, charset));

        return restTemplate;
    }

    static KyoboBookRestTemplate kyoboBookRestTemplate() {
        KyoboBookRestTemplate restTemplate = mock(KyoboBookRestTemplate.class);
        when(restTemplate.findByIsbn(anyString()))
                .thenReturn(
                        new Book("test title", "test description", Arrays.asList(
                                new BookStore("교보문고 : test storeName2", "http://example.com"),
                                new BookStore("교보문고 : test storeName1", "http://example.com"),
                                new BookStore("교보문고 : test storeName3", "http://example.com")
                        )));

        return restTemplate;
    }

}

