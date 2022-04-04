package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@Transactional
@Tag("integration")
class MemberRepositoryDataJdbcAdapterTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("posttest")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    MemberRepositoryDataJdbcAdapter memberRepositoryAdapter;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.sql.init.platform", () -> "postgresql");
    }

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