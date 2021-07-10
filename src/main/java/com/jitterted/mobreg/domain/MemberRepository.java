package com.jitterted.mobreg.domain;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findByGithubUsername(String githubUsername);
}
