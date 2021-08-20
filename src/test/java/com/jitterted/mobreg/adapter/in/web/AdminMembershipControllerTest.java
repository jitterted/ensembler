package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.InMemoryMemberRepository;
import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

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

        assertThat(model.containsAttribute("addMemberForm"))
                .isTrue();

        List<MemberView> members = (List<MemberView>) model.getAttribute("members");

        assertThat(members)
                .extracting(MemberView::firstName)
                .containsOnly("First", "Second");
    }

    @Test
    public void addMemberShouldAddMemberFromFormContent() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        AdminMembershipController adminMembershipController = new AdminMembershipController(memberRepository);

        AddMemberForm addMemberForm = new AddMemberForm();
        addMemberForm.setFirstName("first");
        addMemberForm.setGithubUsername("ghuser");

        String redirectPage = adminMembershipController.addMember(addMemberForm);

        assertThat(redirectPage)
                .isEqualTo("redirect:/admin/members");

        Optional<Member> member = memberRepository.findByGithubUsername("ghuser");
        assertThat(member)
                .isPresent();

        assertThat(member.get().roles())
                .containsOnly("ROLE_USER", "ROLE_MEMBER");
    }

}