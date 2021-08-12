package com.jitterted.mobreg.domain.port;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findByGithubUsername(String githubUsername);

    Optional<Member> findById(MemberId memberId);
}
