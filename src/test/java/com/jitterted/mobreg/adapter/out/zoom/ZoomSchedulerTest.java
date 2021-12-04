package com.jitterted.mobreg.adapter.out.zoom;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleFactory;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

class ZoomSchedulerTest {

    private static final URI CREATE_MEETING_URI = URI.create("https://api.zoom.us/v2/users/me/meetings");

    private final RestTemplate restTemplate = new RestTemplate();

    private final static String ZOOM_JWT = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOm51bGwsImlzcyI6IjFLZHZTcU5rU3FDWnhxbTlaRXl3TVEiLCJleHAiOjE2MzkxNzk0NTAsImlhdCI6MTYzODU3NDY1MH0.3OtvcTdhgHTWugTfdINAvk58AVhrvsc4GvAirSwCosY";

    @Test
    public void create() {
        Ensemble ensemble = EnsembleFactory.withIdOf1AndOneDayInTheFuture();

        ZoomCreateMeetingRequest zoomCreateMeetingRequest = new ZoomCreateMeetingRequest();
        zoomCreateMeetingRequest.duration = 115;
        zoomCreateMeetingRequest.description = "Agenda";
        zoomCreateMeetingRequest.startTime = DateTimeFormatting.formatAsDateTimeForCommonIso8601(ensemble.startDateTime());
        zoomCreateMeetingRequest.topic = ensemble.name();
        zoomCreateMeetingRequest.type = ZoomCreateMeetingRequest.Type.SCHEDULED_MEETING;
        Settings settings = new Settings();
        settings.autoRecording = Settings.AutoRecording.LOCAL;
        settings.hostVideo = true;
        settings.jbhTime = Settings.JbhTime.JOIN_5_MINUTES_BEFORE;
        settings.joinBeforeHost = true;
        settings.muteUponEntry = false;
        settings.participantVideo = false;
        zoomCreateMeetingRequest.settings = settings;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + ZOOM_JWT);
        HttpEntity<ZoomCreateMeetingRequest> requestEntity = new HttpEntity<>(zoomCreateMeetingRequest, headers);

        ResponseEntity<ZoomCreateMeetingResponse> zoomResponse = restTemplate.exchange(
                CREATE_MEETING_URI, HttpMethod.POST, requestEntity, ZoomCreateMeetingResponse.class);

        ZoomCreateMeetingResponse response = zoomResponse.getBody();
        System.out.println(response);

        assertThat(zoomResponse.getStatusCodeValue())
                .isEqualTo(201);
    }
}