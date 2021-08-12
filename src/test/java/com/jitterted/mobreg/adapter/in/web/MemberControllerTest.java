package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.InMemoryHuddleRepository;
import com.jitterted.mobreg.domain.Member;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;

class MemberControllerTest {

    @Test
    public void memberRegistersForHuddleWillBeRegisteredForThatHuddle() throws Exception {
        InMemoryHuddleRepository huddleRepository = new InMemoryHuddleRepository();
        Huddle huddle = huddleRepository.save(new Huddle("Test", ZonedDateTime.now()));
        HuddleService huddleService = new HuddleService(huddleRepository);
        MemberController memberController = new MemberController(huddleService);

        MemberRegisterForm memberRegisterForm = createMemberFormFor(huddle);
        String redirectPage = memberController.register(memberRegisterForm);

        assertThat(redirectPage)
                .isEqualTo("redirect:/member/register");

        assertThat(huddle.participants())
                .extracting(Member::firstName)
                .containsOnly("participant");
    }

    @NotNull
    private MemberRegisterForm createMemberFormFor(Huddle huddle) {
        MemberRegisterForm memberRegisterForm = new MemberRegisterForm();
        memberRegisterForm.setUsername("username");
        memberRegisterForm.setName("participant");
        memberRegisterForm.setHuddleId(huddle.getId().id());
        return memberRegisterForm;
    }

}