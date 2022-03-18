package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class MemberManagementController {

    private final MemberRepository memberRepository;

    public MemberManagementController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/admin/members")
    public String membersView(Model model) {
        List<MemberView> memberViews = memberRepository.findAll()
                                                       .stream()
                                                       .sorted(Comparator.comparingLong(member -> member.getId().id()))
                                                       .map(MemberView::from)
                                                       .toList();
        model.addAttribute("members", memberViews)
             .addAttribute("addMemberForm", new AddMemberForm());
        return "members";
    }

    @PostMapping("/admin/add-member")
    public String addMember(AddMemberForm addMemberForm) {
        Member member = new Member(addMemberForm.getFirstName(),
                                   addMemberForm.getGithubUsername(),
                                   "ROLE_USER", "ROLE_MEMBER");
        memberRepository.save(member);
        return "redirect:/admin/members";
    }
}
