package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.port.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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
                                                       .toList();
        model.addAttribute("members", memberViews);
        return "members";
    }
}
