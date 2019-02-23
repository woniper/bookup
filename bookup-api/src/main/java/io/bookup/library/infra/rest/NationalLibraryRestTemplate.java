package io.bookup.library.infra.rest;

import io.bookup.library.domain.Library;
import io.bookup.library.infra.LibraryRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Component
public class NationalLibraryRestTemplate implements LibraryRepository {

    private final String url;
    private final RestTemplate restTemplate;

    public NationalLibraryRestTemplate(@Value("${bookup.rest.library.url}") String url,
                                       RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "library", key = "#isbn")
    @Override
    public Library findByIsbn(String isbn) {
        ResponseEntity<KorisLibrary> responseEntity = restTemplate.exchange(
                String.format(url, isbn),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                KorisLibrary.class);

        if (isOk(responseEntity)) {
            return new Library(responseEntity.getBody().getItems().stream()
                    .filter(x -> StringUtils.isNotEmpty(x.getLibraryName()) && StringUtils.isNotEmpty(x.getLibraryCode()))
                    .map(x -> {
                        String libraryName = x.getLibraryName();

                        if ("출판시도서목록센터".equals(libraryName)) {
                            libraryName = "국립중앙도서관";
                        }

                        return new Library.Item(libraryName, x.getLibraryCode());
                    })
                    .collect(Collectors.toList()));
        }

        return new Library(Collections.emptyList());
    }

    private boolean isOk(ResponseEntity<KorisLibrary> responseEntity) {
        return Objects.equals(responseEntity.getStatusCode(), HttpStatus.OK) &&
                !CollectionUtils.isEmpty(responseEntity.getBody().getItems());
    }

    @Getter
    @XmlRootElement(name = "METADATA")
    static class KorisLibrary {

        @XmlElement(name = "RECORD")
        private List<Item> items;

        @Getter
        static class Item {
            @XmlElement(name = "LIB_NAME")
            private String libraryName;

            @XmlElement(name = "LIB_CODE")
            private String libraryCode;
        }
    }
}
