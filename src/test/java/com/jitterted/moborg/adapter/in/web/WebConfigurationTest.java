package com.jitterted.moborg.adapter.in.web;

import com.jitterted.moborg.domain.Huddle;
import com.jitterted.moborg.domain.HuddleId;
import com.jitterted.moborg.domain.HuddleService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Disabled("Figure out test for OAuth2-based authN")
public class WebConfigurationTest {

    @MockBean
    HuddleService huddleService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getOfDashboardEndpointReturns200Ok() throws Exception {
        mockMvc.perform(get("/dashboard"))
               .andExpect(status().isOk());
    }

    @Test
    public void getOfHuddleDetailEndpointReturns200Ok() throws Exception {
        when(huddleService.findById(HuddleId.of(13L)))
                .thenReturn(Optional.of(new Huddle("dummy", ZonedDateTime.now())));
        mockMvc.perform(get("/huddle/13"))
               .andExpect(status().isOk());
    }

    @Test
    public void postToScheduleHuddleEndpointRedirects() throws Exception {
        mockMvc.perform(post("/schedule")
                                .param("name", "test")
                                .param("date", "2021-04-30")
                                .param("time", "09:00"))
               .andExpect(status().is3xxRedirection());
    }

}
