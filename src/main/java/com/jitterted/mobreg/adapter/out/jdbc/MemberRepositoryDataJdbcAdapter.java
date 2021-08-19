package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepositoryDataJdbcAdapter implements MemberRepository {

    private final MemberJdbcRepository memberJdbcRepository;

    @Autowired
    public MemberRepositoryDataJdbcAdapter(MemberJdbcRepository memberJdbcRepository) {
        this.memberJdbcRepository = memberJdbcRepository;
    }

    @Override
    public Member save(Member member) {
        MemberEntity memberEntity = MemberEntity.from(member);
        MemberEntity saved = memberJdbcRepository.save(memberEntity);
        return saved.asMember();
    }

    @Override
    public Optional<Member> findByGithubUsername(String githubUsername) {
        Optional<MemberEntity> memberEntity = memberJdbcRepository.findByGithubUsername(githubUsername);
        return memberEntity.map(MemberEntity::asMember);
    }

    @Override
    public Optional<Member> findById(MemberId memberId) {
        Optional<MemberEntity> memberEntity = memberJdbcRepository.findById(memberId.id());
        return memberEntity.map(MemberEntity::asMember);
    }
}
