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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

@Component
public class ZoomScheduler implements VideoConferenceScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZoomScheduler.class);

    private static final URI CREATE_MEETING_URI = URI.create("https://api.zoom.us/v2/users/me/meetings");
    private static final String DELETE_MEETING_URL_TEMPLATE = "https://api.zoom.us/v2/meetings/{meetingId}?schedule_for_reminder=true"; // query param sends email when deleted
    private static final String ZOOM_ACCESS_TOKEN_URL = "https://zoom.us/oauth/token";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${zoom.account.id}")
    private String zoomAccountId;
    @Value("${zoom.client.id}")
    private String zoomClientId;
    @Value("${zoom.client.secret}")
    private String zoomClientSecret;

    @Override
    public ConferenceDetails createMeeting(Ensemble ensemble) {
        ZoomCreateMeetingRequest zoomCreateMeetingRequest = createRequestFor(ensemble);
        HttpHeaders headers = createMeetingApiRequestHeaders();
        HttpEntity<ZoomCreateMeetingRequest> requestEntity =
                new HttpEntity<>(zoomCreateMeetingRequest, headers);

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

    private ZoomAccessTokenResponse fetchAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(zoomClientId, zoomClientSecret); // will base-64 encode it according to RFC 7617
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "account_credentials");
        params.add("account_id", zoomAccountId);
        HttpEntity<Object> httpEntity = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<ZoomAccessTokenResponse> accessTokenResponseResponseEntity =
                restTemplate.postForEntity(ZOOM_ACCESS_TOKEN_URL,
                                           httpEntity,
                                           ZoomAccessTokenResponse.class);

        return accessTokenResponseResponseEntity.getBody();
    }


    @Override
    public boolean deleteMeeting(ConferenceDetails conferenceDetails) {
        HttpEntity<Object> httpEntity = new HttpEntity<>(createMeetingApiRequestHeaders());
        ResponseEntity<Void> responseEntity = restTemplate.exchange(DELETE_MEETING_URL_TEMPLATE,
                                                                    HttpMethod.DELETE,
                                                                    httpEntity,
                                                                    Void.class,
                                                                    conferenceDetails.meetingId());
        int statusCode = responseEntity.getStatusCode().value();
        if (statusCode == 204) {
            LOGGER.info("Deleted Zoom meeting with ID `{}`.", conferenceDetails.meetingId());
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
    private HttpHeaders createMeetingApiRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String bearerToken = fetchAccessToken().accessToken();
        headers.setBearerAuth(bearerToken);
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
