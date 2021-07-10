package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {"GITHUB_OAUTH=dummy"})
@AutoConfigureTestDatabase
@Tag("integration")
class MemberEntityH2SchemaTest {

    @Autowired
    MemberJdbcRepository memberJdbcRepository;

    @Test
    public void memberEntityStoredViaJdbcIsRetrievedAsOriginal() throws Exception {
        Member member = new Member("firstName", "github");

        MemberEntity originalEntity = MemberEntity.from(member);

        MemberEntity savedEntity = memberJdbcRepository.save(originalEntity);

        Optional<MemberEntity> retrievedEntity = memberJdbcRepository.findById(savedEntity.getId());

        assertThat(retrievedEntity)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(originalEntity);

        assertThat(retrievedEntity.get().asMember().firstName())
                .isEqualTo("firstName");

        assertThat(savedEntity.getId())
                .isNotNull();
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