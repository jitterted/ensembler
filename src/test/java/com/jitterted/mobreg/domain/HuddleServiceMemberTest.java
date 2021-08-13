package com.jitterted.mobreg.domain;

import com.jitterted.mobreg.domain.port.HuddleRepository;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleServiceMemberTest {

    @Test
    public void existingMemberRegistersForHuddleThenIsRegisteredMember() throws Exception {
        HuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = new HuddleService(huddleRepository);
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        HuddleId huddleId = huddleRepository.save(huddle).getId();

        MemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = new Member("memberFirstName", "memberGithubUsername");
        MemberId memberId = memberRepository.save(member).getId();

        huddleService.registerMember(huddleId, memberId);

        assertThat(huddle.registeredMembers())
                .containsOnly(memberId);
    }


}