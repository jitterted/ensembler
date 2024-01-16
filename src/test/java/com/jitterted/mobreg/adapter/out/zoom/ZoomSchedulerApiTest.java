package com.jitterted.mobreg.adapter.out.zoom;

import com.jitterted.mobreg.NoSecurityTestConfiguration;
import com.jitterted.mobreg.application.EnsembleService;
import com.jitterted.mobreg.application.MemberService;
import com.jitterted.mobreg.domain.ConferenceDetails;
import com.jitterted.mobreg.domain.Ensemble;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

@Tag("manual")
//@WebMvcTest(
//        controllers = AdminDashboardController.class
//        ,
//        excludeFilters = @ComponentScan.Filter(
//                type = FilterType.ASSIGNABLE_TYPE,
//                classes = WebSecurityConfig.class)
//)
@Import({NoSecurityTestConfiguration.class, ZoomScheduler.class})
@TestPropertySource("file:/Users/tedyoung/.config/spring-boot/spring-boot-devtools.properties")
@ContextConfiguration
class ZoomSchedulerApiTest {

    @MockBean
    EnsembleService ensembleService;

    @MockBean
    MemberService memberService;

    @Autowired
    ZoomScheduler zoomScheduler;

    @Value("${zoom.account.id}")
    private String zoomAccountId;
    @Value("${zoom.client.id}")
    private String zoomClientId;
    @Value("${zoom.client.secret}")
    private String zoomClientSecret;

    @Test
    void accessTokenReturned() {
        ZoomAccessTokenResponse zoomAccessTokenResponse = fetchAccessToken();

        assertThat(zoomAccessTokenResponse.scope())
                .isNotBlank();
        assertThat(zoomAccessTokenResponse.tokenType())
                .isEqualTo("bearer");
        assertThat(zoomAccessTokenResponse.accessToken())
                .isNotBlank();
    }

    @Test
    void schedulerCreateMeetingSuccessfullyCreatesMeeting() {
        Ensemble ensemble = new Ensemble("Test Ensemble", ZonedDateTime.now());

        ConferenceDetails conferenceDetails = zoomScheduler.createMeeting(ensemble);

        assertThat(conferenceDetails.hasValidMeetingId())
                .isTrue();
        assertThat(conferenceDetails.meetingId())
                .isEqualTo("");
    }

    @Test
    void schedulerDeleteMeetingSuccessfullyDeletesMeeting() {
        URI dummyUri = URI.create("https://zoom.us");
        ConferenceDetails conferenceDetails = new ConferenceDetails("86982795177", dummyUri, dummyUri);

        assertThat(zoomScheduler.deleteMeeting(conferenceDetails))
                .isTrue();
    }

    @Test
    void postToDeleteMeetingApiDeletesMeeting() throws Exception {
        String zoomUrl = "https://api.zoom.us/v2/meetings/{meetingId}?schedule_for_reminder=true";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(fetchAccessToken().accessToken());
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Void> responseEntity =
                restTemplate.exchange(zoomUrl,
                                      HttpMethod.DELETE,
                                      httpEntity,
                                      Void.class,
                                      "86408666688");
        System.out.println(responseEntity);
        assertThat(responseEntity.getStatusCode().value())
                .isEqualTo(204);
    }

    private ZoomAccessTokenResponse fetchAccessToken() {
        String tokenUrl = "https://zoom.us/oauth/token";
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
                restTemplate.postForEntity(tokenUrl, httpEntity, ZoomAccessTokenResponse.class);

        return accessTokenResponseResponseEntity.getBody();
    }

}