package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.DefaultMemberService;
import com.jitterted.mobreg.application.DummyMemberRepository;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class EnsembleDetailViewTest {

    @Test
    void ensembleIdIsTranslatedFromDomainIntoView() throws Exception {
        MemberService memberService = new DefaultMemberService(new DummyMemberRepository());
        Ensemble ensemble = new Ensemble("test", ZonedDateTime.now());
        ensemble.setId(EnsembleId.of(23));
        EnsembleDetailView ensembleDetailView = EnsembleDetailView.from(ensemble, memberService);

        assertThat(ensembleDetailView.id())
                .isEqualTo(23);
    }

    @Test
    void viewContainsDetailsForAcceptedAndDeclinedMembersInEnsemble() throws Exception {
        Ensemble ensemble = new Ensemble("view", ZonedDateTime.now());
        ensemble.setId(EnsembleId.of(73));
        MemberRepository memberRepository = new InMemoryMemberRepository();
        MemberService memberService = new DefaultMemberService(memberRepository);
        Member acceptedMember = MemberFactory.createMember(7, "Ace", "acceptated");
        memberRepository.save(acceptedMember);
        ensemble.acceptedBy(acceptedMember.getId());
        Member declinedMember = MemberFactory.createMember(9, "Declan", "declaned");
        memberRepository.save(declinedMember);
        ensemble.declinedBy(declinedMember.getId());

        EnsembleDetailView view = EnsembleDetailView.from(ensemble, memberService);

        assertThat(view.acceptedMembers())
                .first()
                .usingRecursiveComparison()
                .isEqualTo(new MemberView(7L, "Ace", "acceptated", ""));

        assertThat(view.declinedMembers())
                .first()
                .usingRecursiveComparison()
                .isEqualTo(new MemberView(9L, "Declan", "declaned", ""));
    }

}
