package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.HuddleRepository;
import com.jitterted.mobreg.domain.port.Notifier;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HuddleService {
    private final HuddleRepository huddleRepository;
    private final Notifier notifier;

    public HuddleService(HuddleRepository huddleRepository) {
        this.huddleRepository = huddleRepository;
        this.notifier = (description, registrationLink) -> 1;
    }

    public HuddleService(HuddleRepository huddleRepository, Notifier notifier) {
        this.huddleRepository = huddleRepository;
        this.notifier = notifier;
    }

    public void scheduleHuddle(String name, URI zoomMeetingLink, ZonedDateTime zonedDateTime) {
        Huddle huddle = new Huddle(name, zoomMeetingLink, zonedDateTime);
        saveAndNotify(huddle);
    }

    public void scheduleHuddle(String name, ZonedDateTime zonedDateTime) {
        Huddle huddle = new Huddle(name, zonedDateTime);
        saveAndNotify(huddle);
    }

    private void saveAndNotify(Huddle huddle) {
        huddleRepository.save(huddle);
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
        huddle.registerById(memberId);
        huddleRepository.save(huddle);
    }

    public void completeWith(HuddleId huddleId, String recordingLink) {
        Huddle huddle = findById(huddleId)
                .orElseThrow(() -> new HuddleNotFoundException("Huddle ID: " + huddleId.id()));

        huddle.complete();
        huddle.linkToRecordingAt(URI.create(recordingLink));
        huddleRepository.save(huddle);
    }
}
