package io.bookup.book.infra.crawler;

import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
public class LoadFileUtilTest {

    @Test
    public void test() throws IOException {
        String html = LoadFileUtil.load("html/aladin.html", Charset.forName("euc-kr"));
        assertThat(html).isNotBlank();
    }
}
