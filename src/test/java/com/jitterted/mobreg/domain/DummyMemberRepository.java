package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.MemberRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DummyMemberRepository implements MemberRepository {
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

    @Override
    public List<Member> findAll() {
        return Collections.emptyList();
    }
}
