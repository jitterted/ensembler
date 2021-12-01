package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;

import java.time.ZoneId;
import java.util.List;

public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member findById(MemberId memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(MemberNotFoundByIdException::new);
    }

    public Member findByGithubUsername(String username) {
        return memberRepository
                .findByGithubUsername(username.toLowerCase())
                .orElseThrow(() -> new MemberNotFoundByGitHubUsernameException(username));
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public void changeEmail(Member member, String newEmail) {
        member.changeEmailTo(newEmail);
        save(member);
    }

    public void changeTimeZone(Member member, String timeZone) {
        member.changeTimeZoneTo(ZoneId.of(timeZone));
        save(member);
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }
}
