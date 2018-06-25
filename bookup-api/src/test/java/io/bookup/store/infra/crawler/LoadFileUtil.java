package io.bookup.store.infra.crawler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author woniper
 */
class LoadFileUtil {

    static String load(String filePath, Charset charset) throws IOException {
        StringBuilder data = new StringBuilder();
        Files.lines(
                Paths.get(LoadFileUtil.class.getClassLoader().getResource(filePath).getFile()),
                charset).forEach(data::append);

        return data.toString();
    }
}
