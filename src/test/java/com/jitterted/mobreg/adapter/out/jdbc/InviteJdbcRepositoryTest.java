package com.jitterted.mobreg.adapter.out.jdbc;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@Tag("integration")
@SpringBootTest
@Transactional
class InviteJdbcRepositoryTest extends PostgresTestcontainerBase {

    @Autowired
    InviteJdbcRepository inviteJdbcRepository;

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