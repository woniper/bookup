package io.bookup.book.infra.crawler;

import java.io.IOException;
import java.nio.charset.Charset;
import org.jsoup.Jsoup;
import org.mockito.Mockito;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

/**
 * @author woniper
 */
class MockGenerator {

    static HtmlRestTemplate mockRestTemplate(String filePath, Charset charset) throws IOException {
        HtmlRestTemplate restTemplate = mock(HtmlRestTemplate.class);
        Mockito.when(restTemplate.getBodyElement(anyString()))
                .thenReturn(Jsoup.parse(LoadFileUtil.load(filePath, charset)).body());

        return restTemplate;
    }

    static HtmlRestTemplate mockRestTemplate(String filePath) throws Exception {
        return mockRestTemplate(filePath, Charset.forName("UTF-8"));
    }

}

