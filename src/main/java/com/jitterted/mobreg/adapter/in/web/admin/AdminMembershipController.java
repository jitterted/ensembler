package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.domain.Member;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminMembershipController {

    private final MemberRepository memberRepository;

    public AdminMembershipController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/admin/members")
    public String membersView(Model model) {
        List<MemberView> memberViews = memberRepository.findAll()
                                                       .stream()
                                                       .map(MemberView::from)
                                                       .collect(Collectors.toList());
        model.addAttribute("members", memberViews);
        model.addAttribute("addMemberForm", new AddMemberForm());
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
