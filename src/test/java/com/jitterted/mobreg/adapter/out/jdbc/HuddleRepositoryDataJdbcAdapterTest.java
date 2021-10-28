package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
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
class HuddleRepositoryDataJdbcAdapterTest {

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
    HuddleRepositoryDataJdbcAdapter huddleRepositoryAdapter;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Test
    public void savedHuddleCanBeFoundByItsId() throws Exception {
        Huddle huddle = createWithRegisteredMemberHuddleNamed("test huddle");

        Huddle savedHuddle = huddleRepositoryAdapter.save(huddle);

        Optional<Huddle> found = huddleRepositoryAdapter.findById(savedHuddle.getId());

        assertThat(found)
                .isPresent()
                .get()
                .extracting(Huddle::name)
                .isEqualTo("test huddle");
    }

    @Test
    public void newRepositoryReturnsEmptyForFindAll() throws Exception {
        List<Huddle> huddles = huddleRepositoryAdapter.findAll();

        assertThat(huddles)
                .isEmpty();
    }

    @Test
    public void twoSavedHuddlesBothReturnedByFindAll() throws Exception {
        Huddle one = createWithRegisteredMemberHuddleNamed("one");
        Huddle two = createWithRegisteredMemberHuddleNamed("two");

        huddleRepositoryAdapter.save(one);
        huddleRepositoryAdapter.save(two);

        List<Huddle> allHuddles = huddleRepositoryAdapter.findAll();
        assertThat(allHuddles)
                .hasSize(2);

        assertThat(allHuddles.get(0).registeredMembers())
                .hasSize(1)
                .containsOnly(MemberId.of(7L));
        assertThat(allHuddles.get(1).registeredMembers())
                .hasSize(1)
                .containsOnly(MemberId.of(7L));
    }

    @Test
    public void whenHuddleMeetingLinkIsStoredThenIsRetrievedByFind() throws Exception {
        Huddle zoom = new Huddle("With Zoom", URI.create("https://zoom.us/j/123456?pwd=12345"), ZonedDateTime.now());

        HuddleId savedId = huddleRepositoryAdapter.save(zoom).getId();

        Optional<Huddle> found = huddleRepositoryAdapter.findById(savedId);
        assertThat(found)
                .isPresent()
                .get()
                .extracting(Huddle::zoomMeetingLink)
                .extracting(URI::toString)
                .isEqualTo("https://zoom.us/j/123456?pwd=12345");
    }

    @Test
    public void whenHuddleCompletedWithRecordingLinkThenIsStoredSuccessfully() throws Exception {
        Huddle huddle = new Huddle("Completed", ZonedDateTime.now());
        huddle.complete();
        huddle.linkToRecordingAt(URI.create("https://recording.link/database"));

        HuddleId savedId = huddleRepositoryAdapter.save(huddle).getId();

        Huddle found = huddleRepositoryAdapter.findById(savedId).get();

        assertThat(found.isCompleted())
                .isTrue();
        assertThat(found.recordingLink().toString())
                .isEqualTo("https://recording.link/database");
    }

    @NotNull
    private Huddle createWithRegisteredMemberHuddleNamed(String huddleName) {
        Huddle huddle = new Huddle(huddleName, ZonedDateTime.now());
        huddle.register(MemberId.of(7L));
        return huddle;
    }
}