package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.application.port.Notifier;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EnsembleService {
    private final EnsembleRepository ensembleRepository;
    private final Notifier notifier;
    private final MemberRepository memberRepository;

    public EnsembleService(EnsembleRepository ensembleRepository, MemberRepository memberRepository, Notifier notifier) {
        this.ensembleRepository = ensembleRepository;
        this.notifier = notifier;
        this.memberRepository = memberRepository;
    }

    public void scheduleEnsemble(String name, URI zoomMeetingLink, ZonedDateTime zonedDateTime) {
        Ensemble ensemble = new Ensemble(name, zoomMeetingLink, zonedDateTime);
        saveAndNotifyEnsembleScheduled(ensemble);
    }

    public void scheduleEnsemble(String name, ZonedDateTime zonedDateTime) {
        Ensemble ensemble = new Ensemble(name, zonedDateTime);
        saveAndNotifyEnsembleScheduled(ensemble);
    }

    private void saveAndNotifyEnsembleScheduled(Ensemble ensemble) {
        ensembleRepository.save(ensemble);
        notifier.ensembleScheduled(ensemble.name(), URI.create("https://mobreg.herokuapp.com/"));
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

    public void changeNameDateTimeTo(EnsembleId ensembleId, String newName, ZonedDateTime newZoneDateTimeUtc) {
        Ensemble ensemble = findById(ensembleId)
                .orElseThrow(() -> new EnsembleNotFoundException("Ensemble ID: " + ensembleId.id()));
        ensemble.changeNameTo(newName);
        ensemble.changeStartDateTimeTo(newZoneDateTimeUtc);
        ensembleRepository.save(ensemble);
    }
}
