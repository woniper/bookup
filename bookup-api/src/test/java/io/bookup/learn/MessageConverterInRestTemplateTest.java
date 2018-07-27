package io.bookup.learn;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
public class MessageConverterInRestTemplateTest {

    private RestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        this.restTemplate = new RestTemplate();
    }

    @Test
    public void canReadXml() {
        MediaType mediaType = MediaType.APPLICATION_XML;

        for (HttpMessageConverter<?> httpMessageConverter : restTemplate.getMessageConverters()) {
            boolean canRead = httpMessageConverter.canRead(Data.class, mediaType);

            if (canRead) {
                assertThat(httpMessageConverter)
                        .isInstanceOf(Jaxb2RootElementHttpMessageConverter.class);
            }
        }
    }

    @XmlRootElement(name = "ROOT")
    static class Data {

        @XmlElement(name = "NAME")
        private String name;
    }
}
