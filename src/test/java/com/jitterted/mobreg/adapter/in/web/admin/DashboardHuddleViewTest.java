package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.HuddleServiceFactory;
import com.jitterted.mobreg.domain.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.MemberFactory;
import com.jitterted.mobreg.domain.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

public class DashboardHuddleViewTest {

    @Test
    public void detailViewOfExistingHuddleByItsIdIsReturned() throws Exception {
        MemberService memberService = new MemberService(new InMemoryMemberRepository());
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        Huddle savedHuddle = huddleRepository.save(new Huddle("Huddle #1", ZonedDateTime.now()));
        AdminDashboardController adminDashboardController = new AdminDashboardController(huddleService, memberService);

        Model model = new ConcurrentModel();
        adminDashboardController.huddleDetailView(model, 0L);

        HuddleDetailView huddle = (HuddleDetailView) model.getAttribute("huddle");

        assertThat(huddle.name())
                .isEqualTo(savedHuddle.name());
    }

    @Test
    public void detailViewOfExistingHuddleWithOneParticipantReturnsHuddleWithParticipantView() throws Exception {
        MemberService memberService = new MemberService(new InMemoryMemberRepository());
        Member member = MemberFactory.createMember(11, "name", "github");
        memberService.save(member);

        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        Huddle huddle = new Huddle("Huddle #1", ZonedDateTime.now());
        huddle.registerById(member.getId());
        huddleRepository.save(huddle);
        AdminDashboardController adminDashboardController = new AdminDashboardController(huddleService, memberService);

        Model model = new ConcurrentModel();
        adminDashboardController.huddleDetailView(model, huddle.getId().id());

        HuddleDetailView huddleView = (HuddleDetailView) model.getAttribute("huddle");

        assertThat(huddleView.memberViews())
                .hasSize(1);
    }

    @Test
    public void detailViewOfNonExistentHuddleReturns404NotFound() throws Exception {
        MemberService memberService = new MemberService(new InMemoryMemberRepository());
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        HuddleService huddleService = HuddleServiceFactory.createHuddleServiceForTest(huddleRepository);
        AdminDashboardController adminDashboardController = new AdminDashboardController(huddleService, memberService);
        Model model = new ConcurrentModel();

        assertThatThrownBy(() -> {
            adminDashboardController.huddleDetailView(model, 0L);
        }).isInstanceOf(ResponseStatusException.class)
          .extracting("status")
          .isEqualTo(HttpStatus.NOT_FOUND);
    }

}
