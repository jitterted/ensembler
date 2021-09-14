package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class HuddleEntityMappingTest {

    @Test
    public void databaseEntityToDomainIsMappedCorrectly() throws Exception {
        HuddleEntity huddleEntity = new HuddleEntity();
        huddleEntity.setId(19L);
        huddleEntity.setCompleted(true);
        huddleEntity.setRecordingLink("https://recording.link/entity");
        ZonedDateTime now = ZonedDateTime.now();
        huddleEntity.setDateTimeUtc(now.toLocalDateTime());
        huddleEntity.setName("Entity");
        huddleEntity.setZoomMeetingLink("https://zoom.us/entity");
        huddleEntity.setRegisteredMembers(Set.of(new MemberEntityId(13L)));

        Huddle huddle = huddleEntity.asHuddle();

        assertThat(huddle.isCompleted())
                .isTrue();
        assertThat(huddle.name())
                .isEqualTo("Entity");
        assertThat(huddle.recordingLink().toString())
                .isEqualTo("https://recording.link/entity");
        assertThat(huddle.zoomMeetingLink().toString())
                .isEqualTo("https://zoom.us/entity");
        assertThat(huddle.registeredMembers())
                .extracting(MemberId::id)
                .isEqualTo(List.of(13L));
    }

    @Test
    public void domainToDatabaseEntityIsMappedCorrectly() throws Exception {
        ZoneId utcZoneId = ZoneId.of("Z");
        ZonedDateTime utc2021091316000 = ZonedDateTime.of(2021, 9, 13, 16, 0, 0, 0, utcZoneId);
        Huddle huddle = new Huddle("Domain", URI.create("https://zoom.us/"), utc2021091316000);
        huddle.linkToRecordingAt(URI.create("https://recording.link/domain"));
        huddle.registerById(MemberId.of(11L));
        huddle.complete();

        HuddleEntity entity = HuddleEntity.from(huddle);

        assertThat(entity.getName())
                .isEqualTo("Domain");
        assertThat(entity.getDateTimeUtc())
                .isEqualTo(utc2021091316000.toLocalDateTime());
        assertThat(entity.isCompleted())
                .isTrue();
        assertThat(entity.getRecordingLink())
                .isEqualTo("https://recording.link/domain");
        assertThat(entity.getRegisteredMembers())
                .extracting(MemberEntityId::asMemberId)
                .extracting(MemberId::id)
                .isEqualTo(List.of(11L));
    }

}