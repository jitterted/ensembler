package com.jitterted.mobreg.adapter.out.notification.pushover;

import com.jitterted.mobreg.NoSecurityTestConfiguration;
import com.jitterted.mobreg.domain.HuddleService;
import com.jitterted.mobreg.domain.MemberService;
import com.jitterted.mobreg.domain.port.HuddleRepository;
import com.jitterted.mobreg.domain.port.MemberRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@Tag("manual")
// pull in the Pushover App Token property
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Import(NoSecurityTestConfiguration.class)
@TestPropertySource("file:/Users/tedyoung/.config/spring-boot/spring-boot-devtools.properties")
public class PushoverNotificationTest {


    @Autowired
    MemberService memberService;

    @MockBean
    GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Autowired
    HuddleService huddleService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    HuddleRepository huddleRepository;

    @Autowired
    PushoverApiConfig pushoverApiConfig;

    @Test
    public void messageIsSent() throws Exception {
        PushoverMessageDto pushoverMessageDto = new PushoverMessageDto(pushoverApiConfig.getToken(),
                                                                       pushoverApiConfig.getGroupKeyMob1(),
                                                                       "Hello again, from Pushover Notification Test.");

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<Map<String, String>> mapStringStringTypeRef = new ParameterizedTypeReference<>(){};
        ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange("https://api.pushover.net/1/messages.json",
                                                                                   HttpMethod.POST,
                                                                                   new HttpEntity<>(pushoverMessageDto),
                                                                                   mapStringStringTypeRef);

        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get("status"))
                .isEqualTo("1");
        System.out.println(responseEntity.getBody());
    }

}

