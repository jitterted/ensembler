package com.jitterted.moborg.adapter.in.web;

import com.jitterted.moborg.domain.Huddle;
import com.jitterted.moborg.domain.HuddleId;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleDetailViewTest {

    @Test
    public void huddleIdIsTranslatedFromDomainIntoView() throws Exception {
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        huddle.setId(HuddleId.of(23));
        HuddleDetailView huddleDetailView = HuddleDetailView.from(huddle);

        assertThat(huddleDetailView.id())
                .isEqualTo(23);
    }

}