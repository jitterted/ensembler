package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class EnsembleServiceFindTest {

    @Test
    public void whenRepositoryIsEmptyFindReturnsEmptyOptional() throws Exception {
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(new InMemoryEnsembleRepository());

        assertThat(ensembleService.findById(EnsembleId.of(9999)))
                .isEmpty();
    }

    @Test
    public void whenRepositoryHasHuddleFindByItsIdReturnsItInAnOptional() throws Exception {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble savedEnsemble = ensembleRepository.save(new Ensemble("test", ZonedDateTime.now()));
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);

        Optional<Ensemble> foundHuddle = ensembleService.findById(savedEnsemble.getId());

        assertThat(foundHuddle)
                .isNotEmpty();
    }

    @Test
    public void allHuddlesOrderedByDateTimeDescendingIsInCorrectOrder() throws Exception {
        EnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        ensembleService.scheduleEnsemble("two", ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()));
        ensembleService.scheduleEnsemble("one", ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        ensembleService.scheduleEnsemble("three", ZonedDateTime.of(2021, 1, 3, 0, 0, 0, 0, ZoneId.systemDefault()));

        List<Ensemble> ensembles = ensembleService.allEnsemblesByDateTimeDescending();

        assertThat(ensembles)
                .extracting(Ensemble::name)
                .containsExactly("three", "two", "one");
    }

    @Disabled("This is for the filtered view feature")
    @Test
    public void findAllHuddlesForMemberDoesNotReturnCompletedHuddlesWhereMemberIsNotRegistered() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        MemberId memberId = memberRepository.save(new Member("member", "ghuser")).getId();
        EnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        Ensemble ensemble1 = new Ensemble("completed-member", ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()));
        ensemble1.acceptedBy(memberId);
        ensemble1.complete();
        ensemble1 = ensembleRepository.save(ensemble1);

        List<Ensemble> ensembles = ensembleService.findAllForMember(memberId);

        assertThat(ensembles)
                .containsOnly(ensemble1);
    }

    /* Other test cases
            Ensemble ensemble2 = new Ensemble("completed-not-member", ZonedDateTime.of(2021, 9, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        ensemble2 = ensembleRepository.save(ensemble2);
        Ensemble ensemble3 = new Ensemble("not-completed-not-member", ZonedDateTime.of(2021, 10, 3, 0, 0, 0, 0, ZoneId.systemDefault()));
        ensemble3 = ensembleRepository.save(ensemble3);
        EnsembleService ensembleService = new EnsembleService(ensembleRepository);

    */

}
