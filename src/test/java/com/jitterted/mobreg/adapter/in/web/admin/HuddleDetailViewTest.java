package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.DummyMemberRepository;
import com.jitterted.mobreg.application.MemberFactory;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class HuddleDetailViewTest {

    @Test
    public void huddleIdIsTranslatedFromDomainIntoView() throws Exception {
        MemberService memberService = new MemberService(new DummyMemberRepository());
        Huddle huddle = new Huddle("test", ZonedDateTime.now());
        huddle.setId(HuddleId.of(23));
        HuddleDetailView huddleDetailView = HuddleDetailView.from(huddle, memberService);

        assertThat(huddleDetailView.id())
                .isEqualTo(23);
    }

    @Test
    public void viewContainsDetailsForMembersInHuddle() throws Exception {
        Huddle huddle = new Huddle("view", ZonedDateTime.now());
        huddle.setId(HuddleId.of(73));
        MemberRepository memberRepository = new InMemoryMemberRepository();
        MemberService memberService = new MemberService(memberRepository);
        Member member = MemberFactory.createMember(7, "name", "ghusername");
        memberRepository.save(member);
        huddle.acceptedBy(member.getId());

        HuddleDetailView view = HuddleDetailView.from(huddle, memberService);

        MemberView expectedView = new MemberView(7L, "name", "ghusername", "");
        assertThat(view.memberViews())
                .first()
                .usingRecursiveComparison()
                .isEqualTo(expectedView);
    }

}