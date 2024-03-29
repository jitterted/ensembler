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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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
    void membersSeeAllPastEnsemblesForWhichTheyJoinedAsParticipant() {
        Fixture fixture = createFixture(new Ensemble("Past - Joined as Participant",
                                                     ZonedDateTime.now().minusDays(1)));
        Ensemble pastParticipantEnsemble = fixture.ensemble;
        fixture.ensembleService()
               .joinAsParticipant(pastParticipantEnsemble.getId(), fixture.memberId);

        List<Ensemble> ensembles = fixture.ensembleService()
                                          .allInThePastFor(fixture.memberId(), ZonedDateTime.now());

        assertThat(ensembles)
                .containsExactly(pastParticipantEnsemble);
    }

    @Test
    void membersSeeAllPastEnsemblesForWhichTheyJoinedAsSpectators() {
        Fixture fixture = createFixture(new Ensemble("Past - Joined as Spectator",
                                                     ZonedDateTime.now().minusDays(1)));
        Ensemble pastSpectatorEnsemble = fixture.ensemble;
        fixture.ensembleService
                .joinAsSpectator(pastSpectatorEnsemble.getId(), fixture.memberId);

        List<Ensemble> ensembles = fixture.ensembleService()
                                          .allInThePastFor(fixture.memberId(), ZonedDateTime.now());

        assertThat(ensembles)
                .containsExactly(pastSpectatorEnsemble);
    }

    @Test
    void availableIncludesNonCanceledFutureEnsemblesAvailableForRegistrationByMember() {
        // Available to register (might not be available if start time is in 15 min or less)
        // Future = takes place (start date/time) "after" now
        ZonedDateTime now = zoneDateTimeUtc(2024, 1, 11, 10);
        Ensemble futureEnsemble = new Ensemble("Upcoming in 1 day - Available for member to register", now.plusDays(1));
        Fixture fixture = createFixture(futureEnsemble);
        Ensemble canceledEnsemble = fixture.ensembleService
                .scheduleEnsemble("Canceled, but future scheduled to start in 1 day - is NOT available", now.minusDays(1));
        fixture.ensembleService.cancel(canceledEnsemble.getId());
        fixture.ensembleService.scheduleEnsemble("In Progress (1 Hour Ago) - is NOT available", now.minusHours(1));
        fixture.ensembleService.scheduleEnsemble("Past (Yesterday) - is NOT available", now.minusDays(1));

        List<Ensemble> ensembles = fixture.ensembleService.allUpcomingEnsembles(now);

        assertThat(ensembles)
                .containsExactly(futureEnsemble);
    }

    @Test
    void pastEnsemblesForMemberShowOnlyThoseForWhichMemberRegisteredAndNotCanceled() {
        // past ensemble: NOT registered + not-canceled     HIDDEN
        ZonedDateTime now = zoneDateTimeUtc(2024, 1, 11, 10);
        Ensemble hiddenEnsemble = new Ensemble("in the past - not canceled - not registered = HIDDEN", now.minusDays(1));
        Fixture fixture = createFixture(hiddenEnsemble);
        // past ensemble: registered + canceled             HIDDEN
        EnsembleService ensembleService = fixture.ensembleService;
        Ensemble participantButCanceledEnsemble = ensembleService
                .scheduleEnsemble("in the past - CANCELED - REGISTERED = HIDDEN", now.minusDays(1));
        ensembleService.joinAsParticipant(participantButCanceledEnsemble.getId(),
                                          fixture.memberId);
        ensembleService.cancel(participantButCanceledEnsemble.getId());
        // past ensemble: registered + not-canceled         SHOWS
        Ensemble registeredNotCanceledEnsemble = ensembleService
                .scheduleEnsemble("in the past - no canceled - REGISTERED = SHOWN", now.minusDays(1));
        ensembleService.joinAsSpectator(registeredNotCanceledEnsemble.getId(),
                                        fixture.memberId);

        List<Ensemble> pastEnsembles = ensembleService.allInThePastFor(fixture.memberId, now);

        assertThat(pastEnsembles)
                .containsExactly(registeredNotCanceledEnsemble);
    }

    @Nested
    class InProgressEnsemble {
        @Test
        void isEmptyWhenNoEnsembleIsInProgress() {
            Fixture fixture = createFixture(EnsembleFactory.withIdOf1AndOneDayInTheFuture());
            fixture.ensembleService().scheduleEnsemble("In The Past", ZonedDateTime.now().minusDays(1));

            Optional<Ensemble> optionalEnsemble = fixture.ensembleService()
                                                         .inProgressEnsemble(ZonedDateTime.now(), fixture.memberId());

            assertThat(optionalEnsemble)
                    .isEmpty();
        }

        @Test
        void isEmptyWhenEnsembleInProgressAndMemberNotRegistered() {
            ZonedDateTime startDateTime = ZonedDateTime.now().plusMinutes(1);
            Fixture fixture = createFixture(EnsembleFactory.withStartTime(startDateTime));

            ZonedDateTime now = startDateTime.plusSeconds(1);
            Optional<Ensemble> optionalEnsemble = fixture.ensembleService()
                                                         .inProgressEnsemble(now, fixture.memberId());

            assertThat(optionalEnsemble)
                    .isEmpty();
        }

        @Test
        void isPresentWhenInProgressAndMemberIsRegistered() {
            ZonedDateTime startDateTime = ZonedDateTime.now().plusMinutes(1);
            Fixture fixture = createFixture(EnsembleFactory.withStartTime(startDateTime));
            fixture.ensembleService().joinAsParticipant(fixture.ensemble().getId(), fixture.memberId());

            ZonedDateTime now = startDateTime.plusSeconds(1);
            Optional<Ensemble> optionalEnsemble = fixture.ensembleService()
                                                         .inProgressEnsemble(now, fixture.memberId());

            assertThat(optionalEnsemble)
                    .isPresent();
        }
    }


    //
    //-- Encapsulated Setup Fixtures
    //

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