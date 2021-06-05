package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
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