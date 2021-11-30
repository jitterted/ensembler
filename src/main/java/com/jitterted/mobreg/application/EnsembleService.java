package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.HuddleRepository;
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
    private final HuddleRepository huddleRepository;
    private final Notifier notifier;
    private final MemberRepository memberRepository;

    public EnsembleService(HuddleRepository huddleRepository, MemberRepository memberRepository, Notifier notifier) {
        this.huddleRepository = huddleRepository;
        this.notifier = notifier;
        this.memberRepository = memberRepository;
    }

    public void scheduleHuddle(String name, URI zoomMeetingLink, ZonedDateTime zonedDateTime) {
        Ensemble ensemble = new Ensemble(name, zoomMeetingLink, zonedDateTime);
        saveAndNotifyHuddleOpened(ensemble);
    }

    public void scheduleHuddle(String name, ZonedDateTime zonedDateTime) {
        Ensemble ensemble = new Ensemble(name, zonedDateTime);
        saveAndNotifyHuddleOpened(ensemble);
    }

    private void saveAndNotifyHuddleOpened(Ensemble ensemble) {
        huddleRepository.save(ensemble);
        notifier.newHuddleOpened(ensemble.name(), URI.create("https://mobreg.herokuapp.com/"));
    }

    public void triggerHuddleOpenedNotification(Ensemble ensemble) {
        notifier.newHuddleOpened(ensemble.name(), URI.create("https://mobreg.herokuapp.com/"));
    }

    public List<Ensemble> allHuddles() {
        return huddleRepository.findAll();
    }

    public List<Ensemble> allHuddlesByDateTimeDescending() {
        return allHuddles().stream()
                           .sorted(Comparator.comparing(Ensemble::startDateTime).reversed())
                           .toList();
    }

    public Optional<Ensemble> findById(EnsembleId ensembleId) {
        return huddleRepository.findById(ensembleId);
    }

    public void registerMember(EnsembleId ensembleId, MemberId memberId) {
        Ensemble ensemble = findById(ensembleId)
                .orElseThrow(() -> new EnsembleNotFoundException("Ensemble ID: " + ensembleId.id()));
        ensemble.acceptedBy(memberId);
        huddleRepository.save(ensemble);

        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberNotFoundByIdException("Member ID: " + memberId.id()));
        notifier.memberRegistered(ensemble, member);
    }

    public void declineMember(EnsembleId ensembleId, MemberId memberId) {
        Ensemble ensemble = findById(ensembleId)
                .orElseThrow(() -> new EnsembleNotFoundException("Ensemble ID: " + ensembleId.id()));

        ensemble.declinedBy(memberId);

        huddleRepository.save(ensemble);
    }

    public void completeWith(EnsembleId ensembleId, String recordingLink) {
        Ensemble ensemble = findById(ensembleId)
                .orElseThrow(() -> new EnsembleNotFoundException("Ensemble ID: " + ensembleId.id()));

        ensemble.complete();
        ensemble.linkToRecordingAt(URI.create(recordingLink));
        huddleRepository.save(ensemble);
    }

    public List<Ensemble> findAllForMember(MemberId memberId) {
        return null;
    }

    public void changeNameDateTimeTo(EnsembleId ensembleId, String newName, ZonedDateTime newZoneDateTimeUtc) {
        Ensemble ensemble = findById(ensembleId)
                .orElseThrow(() -> new EnsembleNotFoundException("Ensemble ID: " + ensembleId.id()));
        ensemble.changeNameTo(newName);
        ensemble.changeStartDateTimeTo(newZoneDateTimeUtc);
        huddleRepository.save(ensemble);
    }
}
