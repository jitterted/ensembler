package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.FakeMemberRepository;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleDetailViewTest {

    @Test
    public void huddleIdIsTranslatedFromDomainIntoView() throws Exception {
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        huddle.setId(HuddleId.of(23));
        HuddleDetailView huddleDetailView = HuddleDetailView.from(huddle, new FakeMemberRepository());

        assertThat(huddleDetailView.id())
                .isEqualTo(23);
    }

    @Test
    public void viewContainsDetailsForMembersInHuddle() throws Exception {
        Huddle huddle = new Huddle("view", ZonedDateTime.now());
        huddle.setId(HuddleId.of(73));
        MemberRepository memberRepository = new InMemoryMemberRepository();
        Member member = MemberFactory.createMember(7, "name", "ghusername");
        memberRepository.save(member);
        huddle.registerById(member.getId());

        HuddleDetailView view = HuddleDetailView.from(huddle, memberRepository);

        ParticipantView expectedView = new ParticipantView("name", "ghusername");
        assertThat(view.participantViews())
                .first()
                .usingRecursiveComparison()
                .isEqualTo(expectedView);
    }

}