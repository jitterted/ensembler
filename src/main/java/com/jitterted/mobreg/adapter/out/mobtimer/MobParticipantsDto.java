package com.jitterted.mobreg.adapter.out.mobtimer;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberService;

import java.util.List;
import java.util.stream.Collectors;

class MobParticipantsDto {
    private final String type = "mob:update";
    private final List<PersonDto> mob;

    MobParticipantsDto(List<PersonDto> personDtoList) {
        mob = personDtoList;
    }

    public static MobParticipantsDto from(Huddle huddle, MemberService memberService) {
        List<PersonDto> personDtos =
                huddle.registeredMembers().stream()
                      .map(memberService::findById)
                      .map(Member::firstName)
                      .map(PersonDto::new)
                      .collect(Collectors.toList());
        return new MobParticipantsDto(personDtos);
    }

    public String getType() {
        return type;
    }

    public List<PersonDto> getMob() {
        return mob;
    }
}
