package io.bookup.book.infra.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bookup.book.domain.Book;
import io.bookup.book.infra.BookRepository;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author woniper
 */
@Slf4j
@Component
public class NaverRestTemplate implements BookRepository {

    private final RestTemplate restTemplate;
    private final NaverBookProperties properties;
    private final ObjectMapper objectMapper;

    public NaverRestTemplate(RestTemplate restTemplate,
                             NaverBookProperties properties,
                             ObjectMapper objectMapper) {

        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        Optional<NaverBook> naverBook = response(isbn);

        if (naverBook.isPresent()) {
            return naverBook.get().getItems().stream()
                    .map(this::mapBook)
                    .collect(Collectors.toList()).stream()
                    .findFirst();

        }

        return Optional.empty();
    }

    @Override
    public Page<Book> findByTitle(String title, Pageable pageable) {
        Optional<NaverBook> naverBookOptional = response(title, pageable);

        if (naverBookOptional.isPresent()) {
            NaverBook naverBook = naverBookOptional.get();
            List<Book> books = naverBook.getItems().stream()
                    .map(this::mapBook)
                    .collect(Collectors.toList());

            return new PageImpl<>(books, pageable, naverBook.getTotal());
        }

        return new PageImpl<>(Collections.emptyList());
    }

    private Optional<NaverBook> response(String query, Pageable pageable) {
        Assert.notNull(query, "not null query");

        String url;

        if (Objects.nonNull(pageable)) {
            url = properties.createUrl(query, pageable.getOffset(), pageable.getPageSize());
        } else {
            url = properties.createUrl(query);
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(getHeaderMap()),
                String.class);

        if (Objects.equals(HttpStatus.OK, responseEntity.getStatusCode())) {
            try {
                String body = responseEntity
                        .getBody()
                        .replaceAll("<b>", StringUtils.EMPTY)
                        .replaceAll("</b>", StringUtils.EMPTY);

                return Optional.of(objectMapper.readValue(body, NaverBook.class));
            } catch (IOException e) {
                log.error("naver book mapping error", e);
            }
        }

        return Optional.empty();
    }

    MultiValueMap<String, String> getHeaderMap() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("X-Naver-Client-Id", Collections.singletonList(properties.getClientId()));
        headers.put("X-Naver-Client-Secret", Collections.singletonList(properties.getClientSecret()));

        return headers;
    }

    private Optional<NaverBook> response(String query) {
        return response(query, null);
    }

    private Book mapBook(NaverBook.Item item) {
        return Book.builder()
                .title(item.getTitle())
                .description(item.getDescription())
                .link(item.getLink())
                .image(item.getDescription())
                .author(item.getAuthor())
                .price(item.getPrice())
                .publisher(item.getPublisher())
                .publishDate(item.getPubdate())
                .isbn(item.getIsbn())
                .build();
    }

    @Getter
    @NoArgsConstructor
    static class NaverBook {

        private int total;

        private int start;

        private int display;

        private List<Item> items;

        @Getter
        @NoArgsConstructor
        static class Item {
            private String title;
            private String link;
            private String image;
            private String author;
            private String price;
            private String discount;
            private String publisher;
            private String pubdate;
            private String isbn;
            private String description;
        }
    }

}
