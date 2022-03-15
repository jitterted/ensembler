package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.OAuth2UserFactory;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.InviteRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class InvitationControllerTest {

    @Test
    public void validTokenAndAuthnPrincipalCreatesNewMemberAndMarks() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        InviteRepositoryBothExistsAndMarkAsUsedCalledCorrectly inviteRepositoryMock = new InviteRepositoryBothExistsAndMarkAsUsedCalledCorrectly();
        InvitationController invitationController = new InvitationController(memberRepository, inviteRepositoryMock);
        AuthenticatedPrincipal nonMemberAuthn = OAuth2UserFactory.createOAuth2UserWithMemberRole("member_to_become", "ROLE_USER");

        invitationController.processInvitation("token", nonMemberAuthn);

        inviteRepositoryMock.verify();
        assertThat(memberRepository.findByGithubUsername("member_to_become"))
                .isPresent().get()
                .usingRecursiveComparison().comparingOnlyFields("firstName", "githubUsername", "roles")
                .isEqualTo(new Member("", "member_to_become", "ROLE_USER", "ROLE_MEMBER"));
    }

    @Test
    public void nonExistentInviteReturnsInvalidInvitePage() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        InviteRepository inviteRepositoryMock = new InviteRepositoryWhereInviteNeverExists();
        InvitationController invitationController = new InvitationController(memberRepository, inviteRepositoryMock);
        AuthenticatedPrincipal nonMemberAuthn = OAuth2UserFactory.createOAuth2UserWithMemberRole("member_to_become", "ROLE_USER");

        String redirectPage = invitationController.processInvitation("token", nonMemberAuthn);

        assertThat(redirectPage)
                .isEqualTo("invite-invalid");
    }


    @Test
    public void inviteeAlreadyMemberRedirectedToMemberHome() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        InviteRepository inviteRepositoryMock = new InviteRepositoryWhereInviteNeverExists();
        InvitationController invitationController = new InvitationController(memberRepository, inviteRepositoryMock);
        AuthenticatedPrincipal alreadyMemberAuthn = OAuth2UserFactory.createOAuth2UserWithMemberRole("already_member", "ROLE_USER", "ROLE_MEMBER");
        memberRepository.save(new Member("AlreadyMember", "already_member", "ROLE_USER", "ROLE_MEMBER"));

        String redirectPage = invitationController.processInvitation("token", alreadyMemberAuthn);

        assertThat(redirectPage)
                .isEqualTo("redirect:/member/register");
    }


    private static class InviteRepositoryBothExistsAndMarkAsUsedCalledCorrectly implements InviteRepository {
        private boolean markAsUsedWasCalled;
        private boolean existsWasCalled;

        @Override
        public boolean existsByTokenAndGithubUsernameAndWasUsedFalse(String token, String githubUsername) {
            assertThat(token).isEqualTo("token");
            assertThat(githubUsername).isEqualTo("member_to_become");
            existsWasCalled = true;
            return true;
        }

        @Override
        public void markInviteAsUsed(String token, LocalDateTime dateUsedUtc) {
            assertThat(token).isEqualTo("token");
            markAsUsedWasCalled = true;
        }

        public void verify() {
            assertThat(existsWasCalled)
                    .as("exists() was NOT called, but should have been.")
                    .isTrue();
            assertThat(markAsUsedWasCalled)
                    .as("markAsUsed() was NOT called, but should have been.")
                    .isTrue();
        }
    }

    private static class InviteRepositoryWhereInviteNeverExists implements InviteRepository {
        @Override
        public boolean existsByTokenAndGithubUsernameAndWasUsedFalse(String token, String githubUsername) {
            return false;
        }

        @Override
        public void markInviteAsUsed(String token, LocalDateTime dateUsedUtc) {
            fail("This should not have been called.");
        }
    }
}