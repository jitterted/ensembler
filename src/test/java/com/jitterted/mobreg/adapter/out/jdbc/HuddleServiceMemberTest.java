package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.port.HuddleRepository;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@Transactional
@Tag("integration")
class HuddleServiceMemberTest {

    @Autowired
    HuddleRepository huddleRepository;

    @Autowired
    MemberRepository memberRepository;

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
    public void existingMemberRegistersForHuddleThenIsRegisteredMember() throws Exception {
        HuddleService huddleService = new HuddleService(huddleRepository);
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        HuddleId huddleId = huddleRepository.save(huddle).getId();

        Member member = new Member("memberFirstName", "memberGithubUsername");
        MemberId memberId = memberRepository.save(member).getId();

        huddleService.registerMember(huddleId, memberId);

        Optional<Huddle> foundHuddle = huddleRepository.findById(huddleId);

        assertThat(foundHuddle)
                .isPresent();
        assertThat(foundHuddle.get().registeredMembers())
                .containsOnly(memberId);
    }


}