package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.ConferenceDetails;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.FailedToScheduleMeeting;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
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

    public void scheduleEnsemble(String name, URI zoomMeetingLink, ZonedDateTime startDateTime) {
        Ensemble ensemble = new Ensemble(name, zoomMeetingLink, startDateTime);
        saveAndNotifyEnsembleScheduled(ensemble);
    }

    public void scheduleEnsemble(String name, ZonedDateTime startDateTime) {
        Ensemble ensemble = new Ensemble(name, startDateTime);
        saveAndNotifyEnsembleScheduled(ensemble);
    }

    public void scheduleEnsembleWithVideoConference(String name, ZonedDateTime startDateTime) {
        Ensemble ensemble = new Ensemble(name, startDateTime);
        Ensemble savedEnsemble = ensembleRepository.save(ensemble);

        try {
            ConferenceDetails conferenceDetails = videoConferenceScheduler.createMeeting(savedEnsemble);
            savedEnsemble.changeMeetingLinkTo(conferenceDetails.joinUrl());
            saveAndNotifyEnsembleScheduled(savedEnsemble);
        } catch (FailedToScheduleMeeting ftsm) {
            LOGGER.warn("Failed to schedule Ensemble with Video Conference", ftsm);
        }
    }

    public void changeNameDateTimeTo(EnsembleId ensembleId, String newName, ZonedDateTime newZoneDateTimeUtc) {
        Ensemble ensemble = findById(ensembleId)
                .orElseThrow(() -> new EnsembleNotFoundException("Ensemble ID: " + ensembleId.id()));
        ensemble.changeNameTo(newName);
        ensemble.changeStartDateTimeTo(newZoneDateTimeUtc);
        ensembleRepository.save(ensemble);
    }

    private void saveAndNotifyEnsembleScheduled(Ensemble ensemble) {
        ensembleRepository.save(ensemble);
        triggerEnsembleScheduledNotification(ensemble);
    }

    public void triggerEnsembleScheduledNotification(Ensemble ensemble) {
        notifier.ensembleScheduled(ensemble.name(), URI.create("https://mobreg.herokuapp.com/"));
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
        Ensemble ensemble = findById(ensembleId)
                .orElseThrow(() -> new EnsembleNotFoundException("Ensemble ID: " + ensembleId.id()));
        ensemble.acceptedBy(memberId);
        ensembleRepository.save(ensemble);

        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberNotFoundByIdException("Member ID: " + memberId.id()));
        notifier.memberRegistered(ensemble, member);
    }

    public void declineMember(EnsembleId ensembleId, MemberId memberId) {
        Ensemble ensemble = findById(ensembleId)
                .orElseThrow(() -> new EnsembleNotFoundException("Ensemble ID: " + ensembleId.id()));

        ensemble.declinedBy(memberId);

        ensembleRepository.save(ensemble);
    }

    public void completeWith(EnsembleId ensembleId, String recordingLink) {
        Ensemble ensemble = findById(ensembleId)
                .orElseThrow(() -> new EnsembleNotFoundException("Ensemble ID: " + ensembleId.id()));

        ensemble.complete();
        ensemble.linkToRecordingAt(URI.create(recordingLink));
        ensembleRepository.save(ensemble);
    }

    public List<Ensemble> findAllForMember(MemberId memberId) {
        return null;
    }

}
