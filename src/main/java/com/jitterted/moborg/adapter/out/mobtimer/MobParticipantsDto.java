package com.jitterted.moborg.adapter.out.mobtimer;

import com.jitterted.moborg.domain.Huddle;
import com.jitterted.moborg.domain.Participant;

import java.util.List;

class MobParticipantsDto {
    private final String type = "mob:update";
    private final List<PersonDto> mob;

    MobParticipantsDto(List<PersonDto> personDtoList) {
        mob = personDtoList;
    }

    public static MobParticipantsDto from(Huddle huddle) {
        List<PersonDto> personDtos = huddle.participants()
                                           .stream()
                                           .map(Participant::name)
                                           .map(PersonDto::new)
                                           .toList();
        return new MobParticipantsDto(personDtos);
    }

    public String getType() {
        return type;
    }

    public List<PersonDto> getMob() {
        return mob;
    }
}
