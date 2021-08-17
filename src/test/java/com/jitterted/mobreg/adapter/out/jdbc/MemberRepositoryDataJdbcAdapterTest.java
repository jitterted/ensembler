package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Tag("integration")
class MemberRepositoryDataJdbcAdapterTest {

    @Autowired
    MemberRepositoryDataJdbcAdapter memberRepositoryAdapter;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Test
    public void newlyCreatedAndSavedMemberGetsIdAssigned() throws Exception {
        Member member = new Member("first", "githubuser");

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