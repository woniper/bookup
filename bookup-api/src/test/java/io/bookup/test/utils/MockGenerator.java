package io.bookup.test.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

/**
 * @author woniper
 */
public class MockGenerator {

    public static RestTemplate mockRestTemplate(String filePath, Charset charset) throws IOException {
        RestTemplate restTemplate = mock(RestTemplate.class);
        Mockito.when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(LoadHtmlUtil.load(filePath, charset));

        return restTemplate;
    }

    public static RestTemplate mockRestTemplate(String filePath) throws Exception {
        return mockRestTemplate(filePath, Charset.forName("UTF-8"));
    }

}

