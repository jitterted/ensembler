package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.HuddleRepository;
import com.jitterted.mobreg.application.port.InMemoryHuddleRepository;
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
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(new InMemoryHuddleRepository());

        assertThat(huddleService.findById(EnsembleId.of(9999)))
                .isEmpty();
    }

    @Test
    public void whenRepositoryHasHuddleFindByItsIdReturnsItInAnOptional() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        Ensemble savedEnsemble = huddleRepository.save(new Ensemble("test", ZonedDateTime.now()));
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);

        Optional<Ensemble> foundHuddle = huddleService.findById(savedEnsemble.getId());

        assertThat(foundHuddle)
                .isNotEmpty();
    }

    @Test
    public void allHuddlesOrderedByDateTimeDescendingIsInCorrectOrder() throws Exception {
        HuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        huddleService.scheduleHuddle("two", ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()));
        huddleService.scheduleHuddle("one", ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        huddleService.scheduleHuddle("three", ZonedDateTime.of(2021, 1, 3, 0, 0, 0, 0, ZoneId.systemDefault()));

        List<Ensemble> ensembles = huddleService.allHuddlesByDateTimeDescending();

        assertThat(ensembles)
                .extracting(Ensemble::name)
                .containsExactly("three", "two", "one");
    }

    @Disabled("This is for the filtered view feature")
    @Test
    public void findAllHuddlesForMemberDoesNotReturnCompletedHuddlesWhereMemberIsNotRegistered() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        MemberId memberId = memberRepository.save(new Member("member", "ghuser")).getId();
        HuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        Ensemble ensemble1 = new Ensemble("completed-member", ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()));
        ensemble1.acceptedBy(memberId);
        ensemble1.complete();
        ensemble1 = huddleRepository.save(ensemble1);

        List<Ensemble> ensembles = huddleService.findAllForMember(memberId);

        assertThat(ensembles)
                .containsOnly(ensemble1);
    }

    /* Other test cases
            Ensemble huddle2 = new Ensemble("completed-not-member", ZonedDateTime.of(2021, 9, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        huddle2 = huddleRepository.save(huddle2);
        Ensemble huddle3 = new Ensemble("not-completed-not-member", ZonedDateTime.of(2021, 10, 3, 0, 0, 0, 0, ZoneId.systemDefault()));
        huddle3 = huddleRepository.save(huddle3);
        HuddleService huddleService = new HuddleService(huddleRepository);

    */

}
