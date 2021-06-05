package com.jitterted.mobreg.adapter.out.file;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class HuddleDto {
    private long id;
    private String name;
    private ZonedDateTime startDateTime;
    private List<ParticipantDto> participants = new ArrayList<>();

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

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public List<ParticipantDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDto> participants) {
        this.participants = participants;
    }

    public static HuddleDto from(Huddle huddle) {
        HuddleDto huddleDto = new HuddleDto();
        huddleDto.setId(huddle.getId().id());
        huddleDto.setName(huddle.name());
        huddleDto.setStartDateTime(huddle.startDateTime());
        huddleDto.setParticipants(huddle.participants()
                                        .stream()
                                        .map(ParticipantDto::from)
                                        .toList());
        return huddleDto;
    }

    public Huddle asHuddle() {
        Huddle huddle = new Huddle(name, startDateTime);
        huddle.setId(HuddleId.of(id));

        participants.stream()
                    .map(ParticipantDto::asParticipant)
                    .forEach(huddle::register);

        return huddle;
    }
}
