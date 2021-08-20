package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.MemberRepository;

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

    public Member save(Member member) {
        return memberRepository.save(member);
    }
}
