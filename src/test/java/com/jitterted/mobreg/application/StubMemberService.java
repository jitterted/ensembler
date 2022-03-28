package com.jitterted.mobreg.application;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberId;

import java.util.Collections;
import java.util.List;

public class StubMemberService implements MemberService {

    @Override
    public Member findById(MemberId memberId) {
        long id = memberId.id();
        return MemberFactory.createMember(id, "member" + id, "github" + id);
    }

    @Override
    public Member findByGithubUsername(String username) {
        return MemberFactory.createMember(0, "name of" + username, username);
    }

    @Override
    public List<Member> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void changeEmail(Member member, String newEmail) {

    }

    @Override
    public void changeTimeZone(Member member, String timeZone) {

    }

    @Override
    public void changeFirstName(Member member, String newFirstName) {

    }

    @Override
    public Member save(Member member) {
        return member;
    }
}
