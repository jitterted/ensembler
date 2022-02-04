package com.jitterted.mobreg.adapter.out.jdbc;

import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class MemberRepositoryDataJdbcAdapter implements MemberRepository {

    private final MemberJdbcRepository memberJdbcRepository;

    @Autowired
    public MemberRepositoryDataJdbcAdapter(MemberJdbcRepository memberJdbcRepository) {
        this.memberJdbcRepository = memberJdbcRepository;
    }

    @Override
    public Member save(Member member) {
        MemberDbo memberDbo = MemberDbo.from(member);
        MemberDbo saved = memberJdbcRepository.save(memberDbo);
        return saved.asMember();
    }

    @Override
    public Optional<Member> findByGithubUsername(String githubUsername) {
        Optional<MemberDbo> memberEntity = memberJdbcRepository.findByGithubUsername(githubUsername);
        return memberEntity.map(MemberDbo::asMember);
    }

    @Override
    public Optional<Member> findById(MemberId memberId) {
        Optional<MemberDbo> memberEntity = memberJdbcRepository.findById(memberId.id());
        return memberEntity.map(MemberDbo::asMember);
    }

    @Override
    public List<Member> findAll() {
        return StreamSupport.stream(
                memberJdbcRepository.findAll().spliterator(), false)
                            .map(MemberDbo::asMember)
                            .toList();
    }
}
