package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.HuddleRepository;
import com.jitterted.mobreg.domain.port.MemberRepository;
import com.jitterted.mobreg.domain.port.Notifier;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HuddleService {
    private final HuddleRepository huddleRepository;
    private final Notifier notifier;
    private final MemberRepository memberRepository;

    public HuddleService(HuddleRepository huddleRepository, MemberRepository memberRepository, Notifier notifier) {
        this.huddleRepository = huddleRepository;
        this.notifier = notifier;
        this.memberRepository = memberRepository;
    }

    public void scheduleHuddle(String name, URI zoomMeetingLink, ZonedDateTime zonedDateTime) {
        Huddle huddle = new Huddle(name, zoomMeetingLink, zonedDateTime);
        saveAndNotifyHuddleOpened(huddle);
    }

    public void scheduleHuddle(String name, ZonedDateTime zonedDateTime) {
        Huddle huddle = new Huddle(name, zonedDateTime);
        saveAndNotifyHuddleOpened(huddle);
    }

    private void saveAndNotifyHuddleOpened(Huddle huddle) {
        huddleRepository.save(huddle);
        notifier.newHuddleOpened(huddle.name(), URI.create("https://mobreg.herokuapp.com/"));
    }

    public void triggerHuddleOpenedNotification(Huddle huddle) {
        notifier.newHuddleOpened(huddle.name(), URI.create("https://mobreg.herokuapp.com/"));
    }

    public List<Huddle> allHuddles() {
        return huddleRepository.findAll();
    }

    public List<Huddle> allHuddlesByDateTimeDescending() {
        return allHuddles().stream()
                           .sorted(Comparator.comparing(Huddle::startDateTime).reversed())
                           .toList();
    }

    public Optional<Huddle> findById(HuddleId huddleId) {
        return huddleRepository.findById(huddleId);
    }

    public void registerMember(HuddleId huddleId, MemberId memberId) {
        Huddle huddle = findById(huddleId)
                .orElseThrow(() -> new HuddleNotFoundException("Huddle ID: " + huddleId.id()));
        huddle.acceptedBy(memberId);
        huddleRepository.save(huddle);

        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new MemberNotFoundByIdException("Member ID: " + memberId.id()));
        notifier.memberRegistered(huddle, member);
    }

    public void declineMember(HuddleId huddleId, MemberId memberId) {
        Huddle huddle = findById(huddleId)
                .orElseThrow(() -> new HuddleNotFoundException("Huddle ID: " + huddleId.id()));

        huddle.declinedBy(memberId);

        huddleRepository.save(huddle);
    }

    public void completeWith(HuddleId huddleId, String recordingLink) {
        Huddle huddle = findById(huddleId)
                .orElseThrow(() -> new HuddleNotFoundException("Huddle ID: " + huddleId.id()));

        huddle.complete();
        huddle.linkToRecordingAt(URI.create(recordingLink));
        huddleRepository.save(huddle);
    }

    public List<Huddle> findAllForMember(MemberId memberId) {
        return null;
    }

    public void changeNameDateTimeTo(HuddleId huddleId, String newName, ZonedDateTime newZoneDateTimeUtc) {
        Huddle huddle = findById(huddleId)
                .orElseThrow(() -> new HuddleNotFoundException("Huddle ID: " + huddleId.id()));
        huddle.changeNameTo(newName);
        huddle.changeStartDateTimeTo(newZoneDateTimeUtc);
        huddleRepository.save(huddle);
    }
}
