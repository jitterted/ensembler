package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.MemberRepository;

import java.util.Optional;

public class FakeMemberRepository implements MemberRepository {
    @Override
    public Member save(Member member) {
        return null;
    }

    @Override
    public Optional<Member> findByGithubUsername(String githubUsername) {
        return Optional.empty();
    }

    @Override
    public Optional<Member> findById(MemberId memberId) {
        return Optional.empty();
    }
}
