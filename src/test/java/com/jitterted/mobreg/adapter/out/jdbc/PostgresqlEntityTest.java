package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@Disabled
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {"GITHUB_OAUTH=dummy"})
@Tag("container")
@Tag("integration")
class PostgresqlEntityTest {

    @Autowired
    HuddleJdbcRepository huddleJdbcRepository;

    @Autowired
    MemberJdbcRepository memberJdbcRepository;

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

    @Test
    public void huddleEntityStoredViaJdbcIsRetrievedAsOriginal() throws Exception {
//        Huddle original = new Huddle("entity", ZonedDateTime.now());
//        HuddleEntity originalEntity = HuddleEntity.from(original);
        HuddleEntity rawHuddleEntity = new HuddleEntity();
        rawHuddleEntity.setDateTimeUtc(LocalDateTime.now());
        rawHuddleEntity.setName("raw entity");
        rawHuddleEntity.setRegisteredMembers(Set.of(new MemberEntityId(1L), new MemberEntityId(2L)));

//        HuddleEntity savedEntity = huddleJdbcRepository.save(originalEntity);
        HuddleEntity savedEntity = huddleJdbcRepository.save(rawHuddleEntity);

        Optional<HuddleEntity> retrievedEntity = huddleJdbcRepository.findById(savedEntity.getId());

        assertThat(retrievedEntity.get().getRegisteredMembers())
                .hasSize(2);

        assertThat(retrievedEntity)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(rawHuddleEntity);
//                .isEqualTo(originalEntity);

    }

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

        assertThat(retrievedEntity.get().getId())
                .isNotNull();
    }

}