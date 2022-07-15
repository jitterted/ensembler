package com.jitterted.mobreg.application.port;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.MemberState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryMemberRepository implements MemberRepository {
    private final Map<String, MemberState> usernameToMemberMap = new ConcurrentHashMap<>();
    private final Map<MemberId, MemberState> idToMemberMap = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Member save(final Member member) {
        MemberState memberState = member.memento();
        if (memberState.memberId() == null) {
            MemberId newId = MemberId.of(sequence.getAndIncrement());
            Member copy = new Member(memberState);
            copy.setId(newId);
            memberState = copy.memento();
        }
        usernameToMemberMap.put(memberState.githubUsername(), memberState);
        idToMemberMap.put(memberState.memberId(), memberState);
        return new Member(memberState);
    }

    @Override
    public Optional<Member> findByGithubUsername(String githubUsername) {
        MemberState memberState = usernameToMemberMap.get(githubUsername);
        return memberFrom(memberState);
    }

    @Override
    public Optional<Member> findById(MemberId memberId) {
        return memberFrom(idToMemberMap.get(memberId));
    }

    @NotNull
    private Optional<Member> memberFrom(MemberState memberState) {
        if (memberState == null) {
            return Optional.empty();
        }
        return Optional.of(new Member(memberState));
    }

    @Override
    public List<Member> findAll() {
        return idToMemberMap.values()
                            .stream()
                            .map(Member::new)
                            .toList();
    }
}
