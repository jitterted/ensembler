package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJdbcTest(properties = {
        "spring.test.database.replace=NONE",
        "spring.datasource.url=jdbc:tc:postgresql:14:///springboot"
})

@Tag("integration")
class PostgresqlEntityTest extends PostgresTestcontainerBase {

    @Autowired
    EnsembleJdbcRepository ensembleJdbcRepository;

    @Autowired
    MemberJdbcRepository memberJdbcRepository;

    @Test
    void ensembleEntityStoredViaJdbcIsRetrievedWithMembers() throws Exception {
        Ensemble original = new Ensemble("entity", ZonedDateTime.of(2021, 1, 3, 0, 0, 0, 0, ZoneId.systemDefault()));
        original.acceptedBy(MemberId.of(4L));
        original.acceptedBy(MemberId.of(5L));
        original.declinedBy(MemberId.of(73L));
        original.declinedBy(MemberId.of(79L));
        original.joinAsSpectator(MemberId.of(97L));
        original.joinAsSpectator(MemberId.of(101L));
        EnsembleDbo originalEntity = EnsembleDbo.from(original);

        EnsembleDbo savedEntity = ensembleJdbcRepository.save(originalEntity);

        Optional<EnsembleDbo> retrievedEntity = ensembleJdbcRepository.findById(savedEntity.getId());

        assertThat(retrievedEntity)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(originalEntity);

        assertThat(retrievedEntity.get().getAcceptedMembers())
                .extracting(AcceptedMember::asMemberId)
                .extracting(MemberId::id)
                .containsOnly(4L, 5L);

        assertThat(retrievedEntity.get().getDeclinedMembers())
                .extracting(DeclinedMember::asMemberId)
                .extracting(MemberId::id)
                .containsOnly(73L, 79L);

        assertThat(retrievedEntity.get().getSpectatorMembers())
                .extracting(SpectatorMember::asMemberId)
                .extracting(MemberId::id)
                .containsOnly(97L, 101L);
    }

    @Test
    void memberEntityStoredViaJdbcIsRetrievedAsOriginal() throws Exception {
        Member originalMember = new Member("firstName", "github");
        originalMember.changeEmailTo("email@example.com");
        originalMember.changeTimeZoneTo(ZoneId.of("America/Los_Angeles"));

        MemberDbo originalEntity = MemberDbo.from(originalMember);

        MemberDbo savedEntity = memberJdbcRepository.save(originalEntity);

        Optional<MemberDbo> retrievedEntity = memberJdbcRepository.findById(savedEntity.id);

        assertThat(retrievedEntity)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(originalEntity);

        assertThat(savedEntity.id)
                .isNotNull();

        assertThat(retrievedEntity.get().id)
                .isNotNull();

        Member retrievedMember = retrievedEntity.get().asMember();

        assertThat(retrievedMember)
                .usingRecursiveComparison()
                .ignoringFields("id") // because the retrieved member has an ID assigned, but original doesn't
                .isEqualTo(originalMember);
    }

}