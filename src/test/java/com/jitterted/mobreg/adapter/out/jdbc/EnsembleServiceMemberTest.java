package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.EnsembleServiceFactory;
import com.jitterted.mobreg.application.port.HuddleRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
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
class EnsembleServiceMemberTest {

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
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(huddleRepository, memberRepository);
        Ensemble ensemble = new Ensemble("test", ZonedDateTime.now());
        EnsembleId ensembleId = huddleRepository.save(ensemble).getId();

        Member member = new Member("memberFirstName", "memberGithubUsername");
        MemberId memberId = memberRepository.save(member).getId();

        ensembleService.registerMember(ensembleId, memberId);

        Optional<Ensemble> foundHuddle = huddleRepository.findById(ensembleId);

        assertThat(foundHuddle)
                .isPresent();
        assertThat(foundHuddle.get().acceptedMembers())
                .containsOnly(memberId);
    }


}