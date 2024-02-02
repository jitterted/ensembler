package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.adapter.in.web.TestAdminConfiguration;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("mvc")
@WebMvcTest({EnsembleTimerController.class})
@Import(TestAdminConfiguration.class)
@WithMockUser(username = "admin", authorities = {"ROLE_MEMBER", "ROLE_ADMIN"})
class EnsembleTimerControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EnsembleRepository ensembleRepository;

    @Test
    void postToTimerViewRedirects() throws Exception {
        Ensemble ensemble = EnsembleFactory.withStartTimeNowAndIdOf(47);
        ensembleRepository.save(ensemble);
        mockMvc.perform(post("/admin/timer-view")
                                .param("ensembleId", "47")
                                .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin/timer-view/47"));
    }
}