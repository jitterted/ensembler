package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.DummyMemberRepository;
import com.jitterted.mobreg.application.MemberFactory;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleDetailViewTest {

    @Test
    public void huddleIdIsTranslatedFromDomainIntoView() throws Exception {
        MemberService memberService = new MemberService(new DummyMemberRepository());
        Ensemble ensemble = new Ensemble("test", ZonedDateTime.now());
        ensemble.setId(EnsembleId.of(23));
        HuddleDetailView huddleDetailView = HuddleDetailView.from(ensemble, memberService);

        assertThat(huddleDetailView.id())
                .isEqualTo(23);
    }

    @Test
    public void viewContainsDetailsForMembersInHuddle() throws Exception {
        Ensemble ensemble = new Ensemble("view", ZonedDateTime.now());
        ensemble.setId(EnsembleId.of(73));
        MemberRepository memberRepository = new InMemoryMemberRepository();
        MemberService memberService = new MemberService(memberRepository);
        Member member = MemberFactory.createMember(7, "name", "ghusername");
        memberRepository.save(member);
        ensemble.acceptedBy(member.getId());

        HuddleDetailView view = HuddleDetailView.from(ensemble, memberService);

        MemberView expectedView = new MemberView(7L, "name", "ghusername", "");
        assertThat(view.memberViews())
                .first()
                .usingRecursiveComparison()
                .isEqualTo(expectedView);
    }

}