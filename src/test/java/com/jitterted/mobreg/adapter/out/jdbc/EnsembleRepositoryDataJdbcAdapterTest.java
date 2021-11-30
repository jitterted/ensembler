package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.MemberId;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@Transactional
@Tag("integration")
class EnsembleRepositoryDataJdbcAdapterTest {

    // create shared container with a container image name "postgres" and latest major release of PostgreSQL "13"
    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("posttest")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.sql.init.platform", () -> "postgresql");
    }

    @Autowired
    EnsembleRepositoryDataJdbcAdapter huddleRepositoryAdapter;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Test
    public void savedHuddleCanBeFoundByItsId() throws Exception {
        Ensemble ensemble = createWithRegisteredMemberHuddleNamed("test ensemble");

        Ensemble savedEnsemble = huddleRepositoryAdapter.save(ensemble);

        Optional<Ensemble> found = huddleRepositoryAdapter.findById(savedEnsemble.getId());

        assertThat(found)
                .isPresent()
                .get()
                .extracting(Ensemble::name)
                .isEqualTo("test ensemble");
    }

    @Test
    public void newRepositoryReturnsEmptyForFindAll() throws Exception {
        List<Ensemble> ensembles = huddleRepositoryAdapter.findAll();

        assertThat(ensembles)
                .isEmpty();
    }

    @Test
    public void twoSavedHuddlesBothReturnedByFindAll() throws Exception {
        Ensemble one = createWithRegisteredMemberHuddleNamed("one");
        Ensemble two = createWithRegisteredMemberHuddleNamed("two");

        huddleRepositoryAdapter.save(one);
        huddleRepositoryAdapter.save(two);

        List<Ensemble> allEnsembles = huddleRepositoryAdapter.findAll();
        assertThat(allEnsembles)
                .hasSize(2);

        assertThat(allEnsembles.get(0).acceptedMembers())
                .hasSize(1)
                .containsOnly(MemberId.of(7L));
        assertThat(allEnsembles.get(1).acceptedMembers())
                .hasSize(1)
                .containsOnly(MemberId.of(7L));
    }

    @Test
    public void whenHuddleMeetingLinkIsStoredThenIsRetrievedByFind() throws Exception {
        Ensemble zoom = new Ensemble("With Zoom", URI.create("https://zoom.us/j/123456?pwd=12345"), ZonedDateTime.now());

        EnsembleId savedId = huddleRepositoryAdapter.save(zoom).getId();

        Optional<Ensemble> found = huddleRepositoryAdapter.findById(savedId);
        assertThat(found)
                .isPresent()
                .get()
                .extracting(Ensemble::zoomMeetingLink)
                .extracting(URI::toString)
                .isEqualTo("https://zoom.us/j/123456?pwd=12345");
    }

    @Test
    public void whenHuddleCompletedWithRecordingLinkThenIsStoredSuccessfully() throws Exception {
        Ensemble ensemble = new Ensemble("Completed", ZonedDateTime.now());
        ensemble.complete();
        ensemble.linkToRecordingAt(URI.create("https://recording.link/database"));

        EnsembleId savedId = huddleRepositoryAdapter.save(ensemble).getId();

        Ensemble found = huddleRepositoryAdapter.findById(savedId).get();

        assertThat(found.isCompleted())
                .isTrue();
        assertThat(found.recordingLink().toString())
                .isEqualTo("https://recording.link/database");
    }

    @NotNull
    private Ensemble createWithRegisteredMemberHuddleNamed(String huddleName) {
        Ensemble ensemble = new Ensemble(huddleName, ZonedDateTime.now());
        ensemble.acceptedBy(MemberId.of(7L));
        return ensemble;
    }
}