package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Disabled
@SpringBootTest(properties = {"GITHUB_OAUTH=dummy"})
@AutoConfigureTestDatabase
@Tag("integration")
class HuddleEntityH2Test {

    @Autowired
    HuddleJdbcRepository huddleJdbcRepository;

    @Test
    public void huddleEntityStoredViaJdbcIsRetrievedAsOriginal() throws Exception {
        Huddle huddle = new Huddle("entity", ZonedDateTime.now());
        Member member = new Member("name", "github");
        // TODO: use the registerById instead
//        huddle.register(member);

        HuddleEntity originalEntity = HuddleEntity.from(huddle);

        HuddleEntity savedEntity = huddleJdbcRepository.save(originalEntity);

        Optional<HuddleEntity> retrievedEntity = huddleJdbcRepository.findById(savedEntity.getId());

        assertThat(retrievedEntity)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(originalEntity);

        // TODO: check the member ID
        assertThat(retrievedEntity.get().asHuddle().registeredMembers())
                .isNotEmpty();
//                .extracting(Member::firstName)
//                .containsOnly("name");
    }

    @Test
    public void convertToFromZonedDateTimeAndLocalDateTimeWithSeparateZone() throws Exception {
        ZonedDateTime zoned = ZonedDateTime.now();
        LocalDateTime local = zoned.toLocalDateTime();
        String zoneString = zoned.getZone().getId();

        System.out.println(zoned);
        System.out.println(local + ", " + zoneString);

        ZonedDateTime fromLocal = ZonedDateTime.of(local, ZoneId.of(zoneString));
        assertThat(zoned)
                .isEqualTo(fromLocal);
    }

    @Test
    public void convertFromDomainToEntityBackToDomainIsTheSame() throws Exception {
        Huddle original = new Huddle("entity", ZonedDateTime.now());
        original.setId(HuddleId.of(1L));

        HuddleEntity from = HuddleEntity.from(original);

        Huddle converted = from.asHuddle();

        assertThat(converted.name())
                .isEqualTo(original.name());
        assertThat(converted.startDateTime())
                .isEqualTo(original.startDateTime());
    }

}