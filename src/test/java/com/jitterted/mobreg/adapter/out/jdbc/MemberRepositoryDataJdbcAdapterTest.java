package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class MemberRepositoryDataJdbcAdapterTest extends TestContainerBase {

    @Autowired
    MemberRepositoryDataJdbcAdapter memberRepositoryAdapter;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Test
    public void newlyCreatedAndSavedMemberGetsIdAssigned() throws Exception {
        Member member = new Member("first", "githubuser", "ROLE_USER", "ROLE_MEMBER");

        Member savedMember = memberRepositoryAdapter.save(member);

        assertThat(savedMember.getId())
                .isNotNull();
    }

    @Test
    public void memberWithRolesAreStoredThenRetrieved() throws Exception {
        Member member = new Member("first", "githubuser", "ROLE_USER", "ROLE_MEMBER");

        Member savedMember = memberRepositoryAdapter.save(member);

        Optional<Member> foundMember = memberRepositoryAdapter.findById(savedMember.getId());

        assertThat(foundMember)
                .isPresent();

        assertThat(foundMember.get().roles())
                .containsOnly("ROLE_USER", "ROLE_MEMBER");
    }

    @Test
    public void savedMemberCanBeFoundByItsGithubUsername() throws Exception {
        Member member = new Member("first", "ghuser");

        memberRepositoryAdapter.save(member);

        Optional<Member> found = memberRepositoryAdapter.findByGithubUsername("ghuser");

        assertThat(found)
                .isPresent()
                .get()
                .extracting(Member::firstName)
                .isEqualTo("first");
    }

}