package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.MemberId;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

public record InProgressEnsembleView(String name,
                                     String url,
                                     String startTime,
                                     List<String> participants,
                                     List<String> spectators) {

    public static InProgressEnsembleView from(Ensemble ensemble, MemberService memberService) {
        return new InProgressEnsembleView(ensemble.name(),
                                          ensemble.meetingLink().toString(),
                                          ensemble.startDateTime()
                                                  .toLocalTime()
                                                  .format(DateTimeFormatter.ofPattern("hh:mm a")),
                                          namesOf(ensemble.acceptedMembers(), memberService),
                                          namesOf(ensemble.spectators(), memberService));
    }

    public static List<String> namesOf(Stream<MemberId> memberIds, MemberService memberService) {
        return memberIds
                .map(memberId -> memberService.findById(memberId).firstName())
                .toList();
    }
}
