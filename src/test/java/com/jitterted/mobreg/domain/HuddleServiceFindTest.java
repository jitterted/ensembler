package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.HuddleRepository;
import com.jitterted.mobreg.domain.port.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.port.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class HuddleServiceFindTest {

    @Test
    public void whenRepositoryIsEmptyFindReturnsEmptyOptional() throws Exception {
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(new InMemoryHuddleRepository());

        assertThat(huddleService.findById(HuddleId.of(9999)))
                .isEmpty();
    }

    @Test
    public void whenRepositoryHasHuddleFindByItsIdReturnsItInAnOptional() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        Huddle savedHuddle = huddleRepository.save(new Huddle("test", ZonedDateTime.now()));
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);

        Optional<Huddle> foundHuddle = huddleService.findById(savedHuddle.getId());

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

        List<Huddle> huddles = huddleService.allHuddlesByDateTimeDescending();

        assertThat(huddles)
                .extracting(Huddle::name)
                .containsExactly("three", "two", "one");
    }

    @Disabled("This is for the filtered view feature")
    @Test
    public void findAllHuddlesForMemberDoesNotReturnCompletedHuddlesWhereMemberIsNotRegistered() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        MemberId memberId = memberRepository.save(new Member("member", "ghuser")).getId();
        HuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        Huddle huddle1 = new Huddle("completed-member", ZonedDateTime.of(2021, 1, 2, 0, 0, 0, 0, ZoneId.systemDefault()));
        huddle1.acceptedBy(memberId);
        huddle1.complete();
        huddle1 = huddleRepository.save(huddle1);

        List<Huddle> huddles = huddleService.findAllForMember(memberId);

        assertThat(huddles)
                .containsOnly(huddle1);
    }

    /* Other test cases
            Huddle huddle2 = new Huddle("completed-not-member", ZonedDateTime.of(2021, 9, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        huddle2 = huddleRepository.save(huddle2);
        Huddle huddle3 = new Huddle("not-completed-not-member", ZonedDateTime.of(2021, 10, 3, 0, 0, 0, 0, ZoneId.systemDefault()));
        huddle3 = huddleRepository.save(huddle3);
        HuddleService huddleService = new HuddleService(huddleRepository);

    */

}
