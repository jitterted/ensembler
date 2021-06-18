package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Member;

public record ParticipantView(String firstName,
                              String githubUsername) {
    public static ParticipantView from(Member member) {
        return new ParticipantView(member.firstName(),
                                   member.githubUsername());
    }
}
