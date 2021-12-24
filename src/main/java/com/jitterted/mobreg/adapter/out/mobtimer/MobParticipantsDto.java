package com.jitterted.mobreg.adapter.out.mobtimer;

import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.Member;

import java.util.List;

class MobParticipantsDto {
    private final String type = "mob:update";
    private final List<PersonDto> mob;

    MobParticipantsDto(List<PersonDto> personDtoList) {
        mob = personDtoList;
    }

    public static MobParticipantsDto from(Ensemble ensemble, MemberService memberService) {
        List<PersonDto> personDtos =
                ensemble.acceptedMembers()
                        .map(memberService::findById)
                        .map(Member::firstName)
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
