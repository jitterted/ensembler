package com.jitterted.mobreg.adapter.out.jdbc;

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

import static org.assertj.core.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@Tag("container")
@Tag("integration")
@SpringBootTest
class InviteJdbcRepositoryTest {

    @Autowired
    InviteJdbcRepository inviteJdbcRepository;

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
    public void existsForInviteAndUsernameWorksAsExpected() throws Exception {
        InviteDbo inviteDbo = new InviteDbo();
        inviteDbo.setToken("token123");
        inviteDbo.setGithubUsername("HowlingArcticFox");
        inviteDbo.setDateCreatedUtc(LocalDateTime.now());
        inviteDbo.setWasUsed(false);
        InviteDbo usedInviteDbo = new InviteDbo();
        usedInviteDbo.setToken("token001");
        usedInviteDbo.setGithubUsername("echostrike36");
        usedInviteDbo.setDateCreatedUtc(LocalDateTime.now());
        usedInviteDbo.setDateUsedUtc(LocalDateTime.now());
        usedInviteDbo.setWasUsed(true);

        inviteJdbcRepository.save(inviteDbo);
        inviteJdbcRepository.save(usedInviteDbo);

        assertThat(inviteJdbcRepository.existsByTokenAndGithubUsernameIgnoreCaseAndWasUsedFalse("token123", "HowlingArcticFox"))
                .isTrue();
        assertThat(inviteJdbcRepository.existsByTokenAndGithubUsernameIgnoreCaseAndWasUsedFalse("token123", "howlingarcticfox"))
                .isTrue();
        assertThat(inviteJdbcRepository.existsByTokenAndGithubUsernameIgnoreCaseAndWasUsedFalse("token123", "tramstarzz"))
                .isFalse();
        assertThat(inviteJdbcRepository.existsByTokenAndGithubUsernameIgnoreCaseAndWasUsedFalse("token001", "echostrike36"))
                .isFalse();
    }

    @Test
    public void markingInviteAsUsedWillNoLongerBeFound() throws Exception {
        InviteDbo inviteDbo = new InviteDbo();
        inviteDbo.setToken("token007");
        inviteDbo.setGithubUsername("howlingarcticfox");
        inviteDbo.setDateCreatedUtc(LocalDateTime.of(2022, 3, 14, 3, 14));
        inviteDbo.setWasUsed(false);
        inviteJdbcRepository.save(inviteDbo);

        inviteJdbcRepository.markInviteAsUsed("token007", LocalDateTime.now());

        assertThat(inviteJdbcRepository.existsByTokenAndGithubUsernameIgnoreCaseAndWasUsedFalse("token007", "howlingarcticfox"))
                .isFalse();
    }
}