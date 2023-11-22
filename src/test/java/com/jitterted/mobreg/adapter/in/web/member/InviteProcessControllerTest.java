package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.adapter.in.web.OAuth2UserFactory;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.InviteRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ConcurrentModel;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InviteProcessControllerTest {

    @Test
    void validTokenAndAuthnPrincipalCreatesNewMemberAndMarks() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        InviteRepositoryBothExistsAndMarkAsUsedCalledCorrectly inviteRepositoryMock = new InviteRepositoryBothExistsAndMarkAsUsedCalledCorrectly();
        InviteProcessController inviteProcessController = new InviteProcessController(memberRepository, inviteRepositoryMock);
        AuthenticatedPrincipal nonMemberAuthn = OAuth2UserFactory.createOAuth2UserWithMemberRole("Member_To_Become", "ROLE_USER");
        Authentication authentication = createFakeAuthentication(nonMemberAuthn);

        inviteProcessController.processInvitation("token", nonMemberAuthn, new ConcurrentModel());

        assertThat(authentication.isAuthenticated())
                .isFalse();
        inviteRepositoryMock.verify();
        assertThat(memberRepository.findByGithubUsername("Member_To_Become".toLowerCase()))
                .isPresent().get()
                .usingRecursiveComparison().comparingOnlyFields("firstName", "githubUsername", "roles")
                .isEqualTo(new Member("", "member_to_become", "ROLE_USER", "ROLE_MEMBER"));
    }

    @Test
    void nonExistentInviteReturnsInvalidInvitePage() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        InviteRepository inviteRepositoryMock = new InviteRepositoryWhereInviteNeverExists();
        InviteProcessController inviteProcessController = new InviteProcessController(memberRepository, inviteRepositoryMock);
        AuthenticatedPrincipal nonMemberAuthn = OAuth2UserFactory.createOAuth2UserWithMemberRole("member_to_become", "ROLE_USER");

        ConcurrentModel model = new ConcurrentModel();
        String redirectPage = inviteProcessController.processInvitation("token", nonMemberAuthn, model);

        assertThat(redirectPage)
                .isEqualTo("invite-invalid");
        assertThat(model.getAttribute("username"))
                .isEqualTo("member_to_become");
    }

    @NotNull
    private Authentication createFakeAuthentication(AuthenticatedPrincipal nonMemberAuthn) {
        Authentication authentication = new TestingAuthenticationToken(nonMemberAuthn, null);
        authentication.setAuthenticated(true);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        return authentication;
    }


    @Test
    void inviteeAlreadyMemberRedirectedToMemberHome() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        InviteRepository inviteRepositoryMock = new InviteRepositoryWhereInviteNeverExists();
        InviteProcessController inviteProcessController = new InviteProcessController(memberRepository, inviteRepositoryMock);
        AuthenticatedPrincipal alreadyMemberAuthn = OAuth2UserFactory.createOAuth2UserWithMemberRole("already_member", "ROLE_USER", "ROLE_MEMBER");
        memberRepository.save(new Member("AlreadyMember", "already_member", "ROLE_USER", "ROLE_MEMBER"));

        String redirectPage = inviteProcessController.processInvitation("token", alreadyMemberAuthn, null);

        assertThat(redirectPage)
                .isEqualTo("redirect:/member/register");
    }


    private static class InviteRepositoryBothExistsAndMarkAsUsedCalledCorrectly implements InviteRepository {
        private boolean markAsUsedWasCalled;
        private boolean existsWasCalled;

        @Override
        public boolean existsByTokenAndGithubUsernameIgnoreCaseAndWasUsedFalse(String token, String githubUsername) {
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

        @Override
        public void createInviteFor(String githubUsername, String token, LocalDateTime dateCreatedUtc) {
            fail("createInviteFor should not have been called.");
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
        public boolean existsByTokenAndGithubUsernameIgnoreCaseAndWasUsedFalse(String token, String githubUsername) {
            return false;
        }

        @Override
        public void markInviteAsUsed(String token, LocalDateTime dateUsedUtc) {
            fail("markInviteAsUsed should not have been called.");
        }

        @Override
        public void createInviteFor(String githubUsername, String token, LocalDateTime dateCreatedUtc) {
            fail("createInviteFor should not have been called.");
        }
    }
}