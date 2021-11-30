package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZoneOffset;
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
        huddleEntity.setAcceptedMembers(Set.of(new AcceptedMember(13L)));
        huddleEntity.setDeclinedMembers(Set.of(new DeclinedMember(29L)));

        Huddle huddle = huddleEntity.asHuddle();

        assertThat(huddle.isCompleted())
                .isTrue();
        assertThat(huddle.name())
                .isEqualTo("Entity");
        assertThat(huddle.recordingLink().toString())
                .isEqualTo("https://recording.link/entity");
        assertThat(huddle.zoomMeetingLink().toString())
                .isEqualTo("https://zoom.us/entity");
        assertThat(huddle.acceptedMembers())
                .extracting(MemberId::id)
                .isEqualTo(List.of(13L));
        assertThat(huddle.declinedMembers())
                .extracting(MemberId::id)
                .isEqualTo(List.of(29L));
    }

    @Test
    public void domainToDatabaseEntityIsMappedCorrectly() throws Exception {
        ZonedDateTime utc2021091316000 = ZonedDateTime.of(2021, 9, 13, 16, 0, 0, 0, ZoneOffset.UTC);
        Huddle huddle = new Huddle("Domain", URI.create("https://zoom.us/"), utc2021091316000);
        huddle.linkToRecordingAt(URI.create("https://recording.link/domain"));
        huddle.acceptedBy(MemberId.of(11L));
        huddle.declinedBy(MemberId.of(13L));
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
        assertThat(entity.getAcceptedMembers())
                .extracting(AcceptedMember::asMemberId)
                .extracting(MemberId::id)
                .isEqualTo(List.of(11L));
        assertThat(entity.getDeclinedMembers())
                .extracting(DeclinedMember::asMemberId)
                .extracting(MemberId::id)
                .isEqualTo(List.of(13L));
    }

}