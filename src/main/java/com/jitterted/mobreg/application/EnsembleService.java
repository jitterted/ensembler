package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.FailedToScheduleMeeting;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberEnsembleStatus;
import com.jitterted.mobreg.domain.MemberId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.function.Predicate.not;

@SuppressWarnings("ClassCanBeRecord")
public class EnsembleService {
    private final EnsembleRepository ensembleRepository;
    private final Notifier notifier;
    private final MemberRepository memberRepository;
    private final VideoConferenceScheduler videoConferenceScheduler;

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsembleService.class);

    public EnsembleService(EnsembleRepository ensembleRepository, MemberRepository memberRepository,
                           Notifier notifier, VideoConferenceScheduler videoConferenceScheduler) {
        this.ensembleRepository = ensembleRepository;
        this.memberRepository = memberRepository;
        this.notifier = notifier;
        this.videoConferenceScheduler = videoConferenceScheduler;
    }

    public void scheduleEnsemble(String name, URI meetingLink, ZonedDateTime startDateTime) {
        Ensemble ensemble = new Ensemble(name, meetingLink, startDateTime);
        saveAndNotifyEnsembleScheduled(ensemble);
    }

    public Ensemble scheduleEnsemble(String name, ZonedDateTime startDateTime) {
        Ensemble ensemble = new Ensemble(name, startDateTime);
        return saveAndNotifyEnsembleScheduled(ensemble);
    }

    public void scheduleEnsembleWithVideoConference(String name, ZonedDateTime startDateTime) {
        Ensemble ensemble = new Ensemble(name, startDateTime);
        ensemble = saveAndNotifyEnsembleScheduled(ensemble);

        try {
            LOGGER.info("Calling out to Video Conference Scheduler for {}", ensemble.name());
            ConferenceDetails conferenceDetails = videoConferenceScheduler.createMeeting(ensemble);
            ensemble.changeConferenceDetailsTo(conferenceDetails);
            LOGGER.info("Saving Conference Details for {}", ensemble.name());
            ensembleRepository.save(ensemble);
            LOGGER.info("Saved Conference Details for {}", ensemble.name());
        } catch (FailedToScheduleMeeting ftsm) {
            LOGGER.warn("Failed to schedule Ensemble with Video Conference", ftsm);
        }
    }

    public void changeTo(EnsembleId ensembleId, String newName, String newVideoConferenceLink, ZonedDateTime newZoneDateTimeUtc) {
        Ensemble ensemble = findOrThrow(ensembleId);
        ensemble.changeNameTo(newName);
        ensemble.changeStartDateTimeTo(newZoneDateTimeUtc);
        ensemble = ensembleRepository.save(ensemble);

        URI newVideoConferenceUri = ensemble.meetingLink();
        if (newVideoConferenceLink.isBlank()) {
            try {
                ConferenceDetails conferenceDetails = videoConferenceScheduler.createMeeting(ensemble);
                newVideoConferenceUri = conferenceDetails.joinUrl();
            } catch (FailedToScheduleMeeting ftsm) {
                LOGGER.warn("Failed to schedule Ensemble with Video Conference", ftsm);
            }
        } else {
            newVideoConferenceUri = URI.create(newVideoConferenceLink);
        }

        ensemble.changeMeetingLinkTo(newVideoConferenceUri);
        ensembleRepository.save(ensemble);
    }

    private Ensemble saveAndNotifyEnsembleScheduled(Ensemble ensemble) {
        LOGGER.info("Saving Ensemble: {}", ensemble.name());
        Ensemble savedEnsemble = ensembleRepository.save(ensemble);
        LOGGER.info("Ensemble Saved: {}", ensemble.name());
        triggerEnsembleScheduledNotification(ensemble);
        return savedEnsemble;
    }

    public void triggerEnsembleScheduledNotification(Ensemble ensemble) {
        LOGGER.info("Notifying scheduling of Ensemble {}", ensemble.name());
        notifier.ensembleScheduled(ensemble, URI.create("https://ensembler.dev/"));
        LOGGER.info("Done notifying for Ensemble {}", ensemble.name());
    }

    public List<Ensemble> allEnsembles() {
        return ensembleRepository.findAll();
    }

    public List<Ensemble> allEnsemblesByDateTimeDescending() {
        return allEnsembles().stream()
                             .sorted(Comparator.comparing(Ensemble::startDateTime).reversed())
                             .toList();
    }

    public List<Ensemble> ensemblesVisibleFor(MemberId memberId) {
        return allEnsemblesByDateTimeDescending()
                .stream()
                .filter(ensemble ->
                                ensemble.statusFor(memberId, ZonedDateTime.now())
                                        != MemberEnsembleStatus.HIDDEN)
                .toList();
    }

    public List<Ensemble> allUpcomingEnsembles(ZonedDateTime now) {
        return allEnsembles()
                .stream()
                .filter(ensemble -> ensemble.isUpcoming(now))
                .toList();
    }

    public List<Ensemble> allInThePastFor(MemberId memberId, ZonedDateTime now) {
        return allEnsembles()
                .stream()
                .filter(not(Ensemble::isCanceled))
                .filter(ensemble -> ensemble.isRegistered(memberId))
                .filter(ensemble -> ensemble.endTimeIsInThePast(now))
                .toList();
    }

    public Optional<Ensemble> findById(EnsembleId ensembleId) {
        return ensembleRepository.findById(ensembleId);
    }

    public void joinAsParticipant(EnsembleId ensembleId, MemberId memberId) {
        Ensemble savedEnsemble = execute(ensembleId,
                                         ensemble -> ensemble.joinAsParticipant(memberId));

        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberNotFoundByIdException("Member ID: " + memberId.id()));
        notifier.memberAccepted(savedEnsemble, member);
    }

    public void joinAsSpectator(EnsembleId ensembleId, MemberId memberId) {
        execute(ensembleId, ensemble -> ensemble.joinAsSpectator(memberId));
    }

    public void declineMember(EnsembleId ensembleId, MemberId memberId) {
        execute(ensembleId, ensemble -> ensemble.declinedBy(memberId));
    }

    public void completeWith(EnsembleId ensembleId, String recordingLink) {
        Ensemble savedEnsemble = execute(ensembleId, ensemble -> {
            ensemble.complete();
            ensemble.linkToRecordingAt(URI.create(recordingLink));
        });

        notifier.ensembleCompleted(savedEnsemble);
    }

    public void cancel(EnsembleId ensembleId) {
        Ensemble savedEnsemble = execute(ensembleId, Ensemble::cancel);

        deleteVideoConferenceMeeting(savedEnsemble);
    }

    private Ensemble execute(EnsembleId ensembleId, Consumer<Ensemble> action) {
        Ensemble ensemble = findOrThrow(ensembleId);

        action.accept(ensemble);

        return ensembleRepository.save(ensemble);
    }

    private void deleteVideoConferenceMeeting(Ensemble ensemble) {
        ConferenceDetails conferenceDetails = ensemble.conferenceDetails();
        if (!conferenceDetails.hasValidMeetingId()) {
            LOGGER.warn("Could not delete meeting, Ensemble {} does not have a valid meeting ID.", ensemble.name());
            return;
        }
        if (videoConferenceScheduler.deleteMeeting(conferenceDetails)) {
            ensemble.removeConferenceDetails();
            ensembleRepository.save(ensemble);
        } // TODO: else: wasn't deleted: throw exception?
    }

    private Ensemble findOrThrow(EnsembleId ensembleId) {
        return findById(ensembleId)
                .orElseThrow(() -> new EnsembleNotFoundException("Ensemble ID: " + ensembleId.id()));
    }

}
