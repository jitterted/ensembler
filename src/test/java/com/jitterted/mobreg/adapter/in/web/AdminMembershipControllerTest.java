package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class AdminMembershipControllerTest {

    @Test
    public void membersViewReturnsViewsOfAllMembers() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        memberRepository.save(new Member("First", "firstusername", "ROLE_MEMBER"));
        memberRepository.save(new Member("Second", "secondusername", "ROLE_ADMIN"));
        AdminMembershipController adminMembershipController = new AdminMembershipController(memberRepository);

        Model model = new ConcurrentModel();
        adminMembershipController.membersView(model);

        List<MemberView> members = (List<MemberView>) model.getAttribute("members");

        assertThat(members)
                .extracting(MemberView::firstName)
                .containsOnly("First", "Second");
    }

}