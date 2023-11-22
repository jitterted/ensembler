package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.port.InMemoryMemberRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("unchecked")
class MemberManagementControllerTest {

    @Test
    void membersViewReturnsViewsOfAllMembers() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        memberRepository.save(new Member("First", "firstusername", "ROLE_MEMBER"));
        memberRepository.save(new Member("Second", "secondusername", "ROLE_ADMIN"));
        MemberManagementController memberManagementController = new MemberManagementController(memberRepository);

        Model model = new ConcurrentModel();
        memberManagementController.membersView(model);

        assertThat(model.containsAttribute("addMemberForm"))
                .isTrue();

        List<MemberView> members = (List<MemberView>) model.getAttribute("members");

        assertThat(members)
                .extracting(MemberView::firstName)
                .containsOnly("First", "Second");
    }

    @Test
    void addMemberShouldAddMemberFromFormContent() throws Exception {
        MemberRepository memberRepository = new InMemoryMemberRepository();
        MemberManagementController memberManagementController = new MemberManagementController(memberRepository);

        AddMemberForm addMemberForm = new AddMemberForm();
        addMemberForm.setFirstName("first");
        addMemberForm.setGithubUsername("ghuser");


        BindingResult bindingResult = new MapBindingResult(Collections.emptyMap(), "");
        String redirectPage = memberManagementController.addMember(addMemberForm, bindingResult, new ConcurrentModel());

        assertThat(redirectPage)
                .isEqualTo("redirect:/admin/members");

        Optional<Member> member = memberRepository.findByGithubUsername("ghuser");
        assertThat(member)
                .isPresent();

        assertThat(member.get().roles())
                .containsOnly("ROLE_USER", "ROLE_MEMBER");
    }

}