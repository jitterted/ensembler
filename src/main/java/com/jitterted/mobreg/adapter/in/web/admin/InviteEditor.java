package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.out.jdbc.InviteJdbcRepository;
import de.huxhorn.sulky.ulid.ULID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class InviteEditor {

    private final InviteJdbcRepository inviteJdbcRepository;
    private final ULID ulid = new ULID();

    public InviteEditor(InviteJdbcRepository inviteJdbcRepository) {
        this.inviteJdbcRepository = inviteJdbcRepository;
    }

    @GetMapping("/admin/invites")
    public String viewAllInvites(Model model) {
        model.addAttribute("invites", inviteJdbcRepository.findAll())
             .addAttribute("createInviteForm", new CreateInviteForm());
        return "invites";
    }

    @PostMapping("/admin/invites/create")
    public String createInvite(CreateInviteForm createInviteForm) {
        String token = ulid.nextULID();
        inviteJdbcRepository.createInviteFor(createInviteForm.getGithubUsername(), token, LocalDateTime.now());
        return "redirect:/admin/invites";
    }

}

class CreateInviteForm {
    private String githubUsername;
    private String email;

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
