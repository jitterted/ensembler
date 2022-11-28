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
import com.jitterted.mobreg.domain.MemberId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    public void scheduleEnsemble(String name, ZonedDateTime startDateTime) {
        Ensemble ensemble = new Ensemble(name, startDateTime);
        saveAndNotifyEnsembleScheduled(ensemble);
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

    public Optional<Ensemble> findById(EnsembleId ensembleId) {
        return ensembleRepository.findById(ensembleId);
    }

    public void registerMember(EnsembleId ensembleId, MemberId memberId) {
        Ensemble ensemble = findOrThrow(ensembleId);
        ensemble.acceptedBy(memberId);
        ensembleRepository.save(ensemble);

        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberNotFoundByIdException("Member ID: " + memberId.id()));
        notifier.memberAccepted(ensemble, member);
    }

    public void declineMember(EnsembleId ensembleId, MemberId memberId) {
        Ensemble ensemble = findOrThrow(ensembleId);

        ensemble.declinedBy(memberId);

        ensembleRepository.save(ensemble);
    }

    public void completeWith(EnsembleId ensembleId, String recordingLink) {
        Ensemble ensemble = findOrThrow(ensembleId);

        ensemble.complete();
        ensemble.linkToRecordingAt(URI.create(recordingLink));

        ensembleRepository.save(ensemble);

        notifier.ensembleCompleted(ensemble);
    }

    public void cancel(EnsembleId ensembleId) {
        Ensemble ensemble = findOrThrow(ensembleId);

        ensemble.cancel();

        ensembleRepository.save(ensemble);

        deleteVideoConferenceMeeting(ensemble);
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
