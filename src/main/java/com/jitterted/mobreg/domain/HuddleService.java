package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.HuddleRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class HuddleService {
    private final HuddleRepository huddleRepository;

    public HuddleService(HuddleRepository huddleRepository) {
        this.huddleRepository = huddleRepository;
    }

    public void scheduleHuddle(String name, ZonedDateTime zonedDateTime) {
        Huddle huddle = new Huddle(name, zonedDateTime);
        huddleRepository.save(huddle);
    }

    public List<Huddle> allHuddles() {
        return huddleRepository.findAll();
    }

    public Optional<Huddle> findById(HuddleId huddleId) {
        return huddleRepository.findById(huddleId);
    }

    public void registerParticipant(HuddleId huddleId, String name, String githubUsername) {
        Huddle huddle = findById(huddleId)
                .orElseThrow(() -> new HuddleNotFoundException("Huddle ID: " + huddleId.id()));
        Member member = new Member(name, githubUsername);
        huddle.register(member);
        huddleRepository.save(huddle);
    }

    public void registerParticipant(HuddleId huddleId, MemberId memberId) {
        Huddle huddle = findById(huddleId)
                .orElseThrow(() -> new HuddleNotFoundException("Huddle ID: " + huddleId.id()));
        huddle.registerById(memberId);
    }
}
