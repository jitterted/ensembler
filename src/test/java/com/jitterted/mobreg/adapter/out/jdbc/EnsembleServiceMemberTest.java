package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.EnsembleServiceFactory;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Tag("integration")
class EnsembleServiceMemberTest extends PostgresTestcontainerBase {

    @Autowired
    EnsembleRepository ensembleRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void existingMemberRegistersForEnsembleThenIsRegisteredMember() throws Exception {
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository, memberRepository);
        Ensemble ensemble = new Ensemble("test", ZonedDateTime.now());
        EnsembleId ensembleId = ensembleRepository.save(ensemble).getId();

        Member member = new Member("memberFirstName", "memberGithubUsername");
        MemberId memberId = memberRepository.save(member).getId();

        ensembleService.registerMember(ensembleId, memberId);

        Optional<Ensemble> foundEnsemble = ensembleRepository.findById(ensembleId);

        assertThat(foundEnsemble)
                .isPresent();
        assertThat(foundEnsemble.get().acceptedMembers())
                .containsOnly(memberId);
    }


}