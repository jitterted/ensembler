package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;

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
