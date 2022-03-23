package com.jitterted.mobreg.adapter.out.zoom;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@Tag("manual")
class ZoomSchedulerDeleteTest {

    private String zoomJwt = "";

    @Test
    public void deleteMeetingThenDeletesTheMeeting() throws Exception {
        String zoomUrl = "https://api.zoom.us/v2/meetings/{meetingId}?schedule_for_reminder=true";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + zoomJwt);
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Void> responseEntity =
                restTemplate.exchange(zoomUrl,
                                      HttpMethod.DELETE,
                                      httpEntity,
                                      Void.class,
                                      "82526380374");
        System.out.println(responseEntity);
        assertThat(responseEntity.getStatusCode().value())
                .isEqualTo(204);
    }

}