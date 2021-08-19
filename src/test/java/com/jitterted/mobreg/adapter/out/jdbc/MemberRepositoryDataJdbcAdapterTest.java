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