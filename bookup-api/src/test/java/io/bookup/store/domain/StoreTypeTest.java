package io.bookup.store.domain;

import io.bookup.store.domain.Store.StoreType;
import io.bookup.store.infra.crawler.AladinCrawler;
import io.bookup.store.infra.crawler.BandinLunisCrawler;
import io.bookup.store.infra.rest.KyoboRestTemplate;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author woniper
 */
public class StoreTypeTest {

    @Test
    public void typeEquals_aladinCrawler() {
        StoreType type = StoreType.ALADIN;

        boolean equals = type.typeEquals(AladinCrawler.class);

        assertThat(equals).isTrue();
    }

    @Test
    public void typeEquals_bandinlunisCrawler() {
        StoreType type = StoreType.BANDI;

        boolean equals = type.typeEquals(BandinLunisCrawler.class);

        assertThat(equals).isTrue();
    }

    @Test
    public void typeEquals_kyoboRestTemplate() {
        StoreType type = StoreType.KYOBO;

        boolean equals = type.typeEquals(KyoboRestTemplate.class);

        assertThat(equals).isTrue();
    }

    @Test
    public void typeEquals_notEquals() {
        StoreType type = StoreType.KYOBO;

        boolean equals = type.typeEquals(BandinLunisCrawler.class);

        assertThat(equals).isFalse();
    }
}
