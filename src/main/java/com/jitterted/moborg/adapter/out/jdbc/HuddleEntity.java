package com.jitterted.moborg.adapter.out.jdbc;

import com.jitterted.moborg.domain.Huddle;
import com.jitterted.moborg.domain.HuddleId;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// DTO for Huddle to be stored in the database
public class HuddleEntity {
    @Id
    private long id;

    private String name;
    private LocalDateTime localDateTime;
    private String zoneId;

    private Set<ParticipantEntity> participants = new HashSet<>();

    public static HuddleEntity from(Huddle huddle) {
        HuddleEntity huddleEntity = new HuddleEntity();
        if (huddle.getId() != null) {
            huddleEntity.setId(huddle.getId().id());
        }
        huddleEntity.setName(huddle.name());
        huddleEntity.setLocalDateTime(huddle.startDateTime().toLocalDateTime());
        huddleEntity.setZoneId(huddle.startDateTime().getZone().getId());

        huddleEntity.setParticipants(huddle.participants()
                                           .stream()
                                           .map(ParticipantEntity::from)
                                           .collect(Collectors.toSet()));

        return huddleEntity;
    }

    public Huddle asHuddle() {
        ZonedDateTime startDateTime = ZonedDateTime.of(localDateTime, ZoneId.of(zoneId));
        Huddle huddle = new Huddle(name, startDateTime);
        huddle.setId(HuddleId.of(id));

        participants.stream()
                    .map(ParticipantEntity::asParticipant)
                    .forEach(huddle::register);

        return huddle;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Set<ParticipantEntity> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<ParticipantEntity> participants) {
        this.participants = participants;
    }
}
