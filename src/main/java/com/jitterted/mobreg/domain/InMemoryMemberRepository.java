package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.MemberRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryMemberRepository implements MemberRepository {
    private final Map<String, Member> usernameToMemberMap = new ConcurrentHashMap<>();
    private final Map<MemberId, Member> idToMemberMap = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Member save(Member member) {
        if (member.getId() == null) {
            member.setId(MemberId.of(sequence.getAndIncrement()));
        }
        usernameToMemberMap.put(member.githubUsername(), member);
        idToMemberMap.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findByGithubUsername(String githubUsername) {
        return Optional.ofNullable(
                usernameToMemberMap.get(githubUsername));
    }

    @Override
    public Member findById(MemberId memberId) {
        return idToMemberMap.get(memberId);
    }
}
