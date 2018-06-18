package io.bookup.book.infra.crawler;

import io.bookup.book.domain.KyoboBookStore;
import io.bookup.book.infra.rest.KyoboBookRestTemplate;
import io.bookup.book.infra.rest.KyoboProperties;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
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
                        Optional.of(new KyoboBookStore(Arrays.asList(
                                new KyoboBookStore.Item("01", 2, "잠실점"),
                                new KyoboBookStore.Item("02", 2, "송도점"),
                                new KyoboBookStore.Item("03", 2, "구로점")
                        ))));

        return restTemplate;
    }

    static KyoboProperties kyoboProperties() {
        KyoboProperties properties = new KyoboProperties();
        properties.setUrl("http://mkiosk.kyobobook.co.kr/kiosk/product/ajaxOtherStockQty.ink?site=&ejkGb=KOR&barcode=%s");
        properties.setStoreUrl("http://mkiosk.kyobobook.co.kr/kiosk/search/searchMain.ink?site=%s&isbn=%s");

        return properties;
    }

}

