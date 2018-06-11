package io.bookup.test.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
public class LoadHtmlUtilTest {

    @Test
    public void test() throws IOException {
        String html = LoadHtmlUtil.load("html/aladin.html", Charset.forName("euc-kr"));
        assertThat(html).isNotBlank();
    }
}
