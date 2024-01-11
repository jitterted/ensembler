package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.DummyNotifier;
import com.jitterted.mobreg.application.port.DummyVideoConferenceScheduler;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryEnsembleRepository;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jitterted.mobreg.domain.ZonedDateTimeFactory.zoneDateTimeUtc;
import static org.assertj.core.api.Assertions.*;

class EnsembleServiceTest {

    @Test
    void joinAsSpectatorDelegatesToEnsemble() {
        InMemoryEnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = EnsembleFactory.withStartTimeNow();
        ensembleRepository.save(ensemble);
        ensembleRepository.resetSaveCount();
        EnsembleService ensembleService = EnsembleServiceFactory.createServiceWith(ensembleRepository);
        MemberId memberId = MemberId.of(37);

        ensembleService.joinAsSpectator(ensemble.getId(), memberId);

        assertThat(ensembleRepository.savedEnsembles())
                .hasSize(1);
        assertThat(ensembleRepository.savedEnsembles().get(0).spectators())
                .containsExactly(memberId);
    }

    @Test
    void showCanceledEnsemblesFromThePastForJoinedParticipants() {
        Fixture fixture = createFixture(new Ensemble("Canceled - Joined as Participant",
                                                     ZonedDateTime.now().minusDays(1)));
        Ensemble canceledParticipantEnsemble = fixture.ensemble();
        EnsembleService ensembleService = fixture.ensembleService();
        ensembleService.scheduleEnsemble("Past - Member Not Joined",
                                         ZonedDateTime.now().minusDays(1));
        ensembleService.joinAsParticipant(canceledParticipantEnsemble.getId(), fixture.memberId());
        ensembleService.cancel(canceledParticipantEnsemble.getId());

        List<Ensemble> ensembles = ensembleService.ensemblesVisibleFor(fixture.memberId());

        assertThat(ensembles)
                .extracting(Ensemble::name)
                .containsExactly("Canceled - Joined as Participant");
    }

    @Test
    void showCanceledEnsemblesFromThePastForJoinedSpectators() {
        Fixture fixture = createFixture(new Ensemble("Canceled - Joined as Spectator",
                                                     ZonedDateTime.now().minusDays(1)));
        Ensemble canceledSpectatorEnsemble = fixture.ensemble();
        EnsembleService ensembleService = fixture.ensembleService();
        ensembleService.scheduleEnsemble("Past - Member Not Joined",
                                         ZonedDateTime.now().minusDays(1));
        ensembleService.joinAsSpectator(canceledSpectatorEnsemble.getId(), fixture.memberId());
        ensembleService.cancel(canceledSpectatorEnsemble.getId());

        List<Ensemble> ensembles = ensembleService.ensemblesVisibleFor(fixture.memberId());

        assertThat(ensembles)
                .extracting(Ensemble::name)
                .containsExactly("Canceled - Joined as Spectator");
    }

    @Test
    void membersSeeAllPastEnsemblesForWhichTheyJoinedAsParticipant() {
        Fixture fixture = createFixture(new Ensemble("Past - Joined as Participant",
                                                     ZonedDateTime.now().minusDays(1)));
        Ensemble pastParticipantEnsemble = fixture.ensemble;
        fixture.ensembleService()
               .joinAsParticipant(pastParticipantEnsemble.getId(), fixture.memberId);

        List<Ensemble> ensembles = fixture.ensembleService.ensemblesVisibleFor(fixture.memberId);

        assertThat(ensembles)
            .containsExactly(pastParticipantEnsemble);
    }

    @Test
    void membersSeeAllPastEnsemblesForWhichTheyJoinedAsSpectators() {
        Fixture fixture = createFixture(new Ensemble("Past - Joined as Spectator",
                                                     ZonedDateTime.now().minusDays(1)));
        Ensemble pastSpectatorEnsemble = fixture.ensemble;
        fixture.ensembleService()
               .joinAsSpectator(pastSpectatorEnsemble.getId(), fixture.memberId);

        List<Ensemble> ensembles = fixture.ensembleService.ensemblesVisibleFor(fixture.memberId);

        assertThat(ensembles)
            .containsExactly(pastSpectatorEnsemble);
    }

    @Test
    void availableIncludesFutureEnsemblesAvailableForRegistrationByMember() {
        // Available to register (might not be available if start time is in 15 min or less)
        // Future = takes place (start date/time) "after" now
        ZonedDateTime now = zoneDateTimeUtc(2024, 1, 11, 10);
        Ensemble futureEnsemble = new Ensemble("Upcoming in 1 day - Possible for member to register", now.plusDays(1));
        Fixture fixture = createFixture(futureEnsemble);
        fixture.ensembleService.scheduleEnsemble("In Progress (1 Hour Ago) - Is not upcoming", now.minusHours(1));
        fixture.ensembleService.scheduleEnsemble("Past (Yesterday) - Is not upcoming", now.minusDays(1));

        List<Ensemble> ensembles = fixture.ensembleService.allAvailableForRegistration(now);

        assertThat(ensembles)
            .containsExactly(futureEnsemble);
    }

    //-- Encapsulated Setup Fixtures

    @NotNull
    private static Fixture createFixture(Ensemble ensembleToSave) {
        EnsembleRepository ensembleRepository = new InMemoryEnsembleRepository();
        Ensemble ensemble = ensembleRepository.save(ensembleToSave);

        MemberRepository memberRepository = new InMemoryMemberRepository();

        EnsembleService ensembleService = new EnsembleService(ensembleRepository,
                                                              memberRepository,
                                                              new DummyNotifier(),
                                                              new DummyVideoConferenceScheduler());
        MemberService memberService = new DefaultMemberService(memberRepository);

        MemberId memberId = memberService
                .save(new Member("participant", "ghuser", "ROLE_MEMBER"))
                .getId();

        return new Fixture(ensemble, memberId, ensembleService);
    }

    private record Fixture(Ensemble ensemble,
                           MemberId memberId,
                           EnsembleService ensembleService) {
    }

}