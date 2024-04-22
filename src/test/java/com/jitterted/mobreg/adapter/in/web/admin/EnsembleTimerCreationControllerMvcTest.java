package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.in.web.TestAdminConfiguration;
import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("mvc")
@WebMvcTest({EnsembleTimerCreationController.class})
@Import(TestAdminConfiguration.class)
@WithMockUser(username = "admin", authorities = {"ROLE_MEMBER", "ROLE_ADMIN"})
class EnsembleTimerCreationControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EnsembleRepository ensembleRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EnsembleTimerHolder ensembleTimerHolder;

    @Test
    void postToCreateTimerRedirects() throws Exception {
        createAndSaveEnsembleInRepositoryForId(257);
        mockMvc.perform(post("/admin/create-timer/257").with(csrf()))
               .andExpect(status().is3xxRedirection());
    }

    // -- Encapsulated Setup

    private void createAndSaveEnsembleInRepositoryForId(long ensembleId) {
        Ensemble ensemble = new EnsembleBuilder().id(ensembleId)
                                                 .startsNow()
                                                 .build();
        new TestEnsembleServiceBuilder()
                .withEnsembleRepository(ensembleRepository)
                .withMemberRepository(memberRepository)
                .saveEnsemble(ensemble)
                .withThreeParticipants();
    }
}