package com.jitterted.moborg.domain;

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

    public void registerParticipant(HuddleId huddleId, String name, String githubUsername, String discordUsername) {
        Huddle huddle = findById(huddleId)
                .orElseThrow(HuddleNotFoundException::new);
        Participant participant = new Participant(name, githubUsername, "", discordUsername, false);
        huddle.register(participant);
        huddleRepository.save(huddle);
    }
}
