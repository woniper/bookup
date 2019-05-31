package io.bookup.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.bookup.store.infra.StoreRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * @author woniper
 */
@Getter
public class Store {

    @JsonIgnore
    public static final Collection<Store> EMPTY = Collections.emptyList();

    private String storeName;
    private String href;

    public Store(String storeName, String href) {
        this.storeName = storeName;
        this.href = href;
    }

    public enum StoreType {

        ALADIN(AladinCrawler.class),
        BANDI(BandinLunisCrawler.class),
        KYOBO(KyoboClient.class);

        private Class<? extends StoreRepository> repositoryClass;

        StoreType(Class<? extends StoreRepository> repositoryClass) {
            this.repositoryClass = repositoryClass;
        }

        public boolean typeEquals(Class<? extends StoreRepository> repositoryClass) {
            return Objects.equals(this.repositoryClass.getName(), repositoryClass.getName());
        }

        public static Set<StoreType> getTypes() {
            return Stream.of(values()).collect(Collectors.toSet());
        }
    }

}
