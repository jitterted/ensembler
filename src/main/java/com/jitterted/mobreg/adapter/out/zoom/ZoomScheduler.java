package com.jitterted.mobreg.adapter.out.zoom;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.application.port.FailedToScheduleMeeting;
import com.jitterted.mobreg.application.port.VideoConferenceScheduler;
import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

@Component
public class ZoomScheduler implements VideoConferenceScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZoomScheduler.class);

    private static final URI CREATE_MEETING_URI = URI.create("https://api.zoom.us/v2/users/me/meetings");
    private static final String ZOOM_API_MEETING_URL_TEMPLATE = "https://api.zoom.us/v2/meetings/{meetingId}";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${zoom.jwt}")
    private String zoomJwt;

    @Override
    public ConferenceDetails createMeeting(Ensemble ensemble) {
        ZoomCreateMeetingRequest zoomCreateMeetingRequest = createRequestFor(ensemble);
        HttpHeaders headers = createRequestHeaders();
        HttpEntity<ZoomCreateMeetingRequest> requestEntity = new HttpEntity<>(zoomCreateMeetingRequest, headers);

        ResponseEntity<ZoomCreateMeetingResponse> zoomResponse = postRequest(requestEntity);

        if (!zoomResponse.getStatusCode().is2xxSuccessful()) {
            throw new FailedToScheduleMeeting(zoomResponse.toString());
        }

        ZoomCreateMeetingResponse createMeetingResponse = zoomResponse.getBody();

        if (createMeetingResponse == null) {
            throw new FailedToScheduleMeeting(zoomResponse.toString());
        }

        return new ConferenceDetails(String.valueOf(createMeetingResponse.id),
                                     URI.create(createMeetingResponse.startUrl),
                                     URI.create(createMeetingResponse.joinUrl));
    }

    @Override
    public boolean deleteMeeting(ConferenceDetails conferenceDetails) {
        HttpEntity<Object> httpEntity = new HttpEntity<>(createRequestHeaders());
        ResponseEntity<Void> responseEntity = restTemplate.exchange(ZOOM_API_MEETING_URL_TEMPLATE,
                                                                    HttpMethod.DELETE,
                                                                    httpEntity,
                                                                    Void.class,
                                                                    conferenceDetails.meetingId());
        int statusCode = responseEntity.getStatusCode().value();
        if (statusCode == 204) {
            return true;
        } else {
            LOGGER.warn("Unable to delete Zoom meeting with ID `{}`. Status Code was: {}", conferenceDetails.meetingId(), statusCode);
            return false;
        }
    }

    private ResponseEntity<ZoomCreateMeetingResponse> postRequest(HttpEntity<ZoomCreateMeetingRequest> requestEntity) {
        try {
            return restTemplate.exchange(
                    CREATE_MEETING_URI, HttpMethod.POST, requestEntity, ZoomCreateMeetingResponse.class);
        } catch (RestClientException e) {
            // note: the exception is thrown due to any non-2xx response
            LOGGER.error("Failed to Create Meeting: " + requestEntity.getBody().topic, e);
            throw new FailedToScheduleMeeting(e);
        }
    }

    @NotNull
    private HttpHeaders createRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + zoomJwt);
        return headers;
    }

    @NotNull
    private ZoomCreateMeetingRequest createRequestFor(Ensemble ensemble) {
        ZoomCreateMeetingRequest zoomCreateMeetingRequest = new ZoomCreateMeetingRequest();
        zoomCreateMeetingRequest.defaultPassword = true; // apparently if this is false, then "waitingRoom" gets set to true
        zoomCreateMeetingRequest.description = "Description"; // no idea where this shows up
        zoomCreateMeetingRequest.duration = 115; // in minutes
        zoomCreateMeetingRequest.startTime = DateTimeFormatting.formatAsDateTimeForCommonIso8601(ensemble.startDateTime());
        zoomCreateMeetingRequest.topic = ensemble.name();
        zoomCreateMeetingRequest.type = ZoomCreateMeetingRequest.Type.SCHEDULED_MEETING;
        zoomCreateMeetingRequest.settings = createSettings();
        return zoomCreateMeetingRequest;
    }

    private Settings createSettings() {
        Settings settings = new Settings();
        settings.autoRecording = Settings.AutoRecording.LOCAL;
        settings.hostVideo = true;
        settings.jbhTime = Settings.JbhTime.JOIN_5_MINUTES_BEFORE;
        settings.joinBeforeHost = true;
        settings.muteUponEntry = false;
        settings.participantVideo = false;
        settings.waitingRoom = false;
        return settings;
    }

}
