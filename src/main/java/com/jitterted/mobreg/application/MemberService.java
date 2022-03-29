package com.jitterted.mobreg.application;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;

import java.util.List;

public interface MemberService {
    Member findById(MemberId memberId);

    Member findByGithubUsername(String username);

    List<Member> findAll();

    void changeEmail(Member member, String newEmail);

    void changeTimeZone(Member member, String timeZone);

    void changeFirstName(Member member, String newFirstName);

    Member save(Member member);
}
