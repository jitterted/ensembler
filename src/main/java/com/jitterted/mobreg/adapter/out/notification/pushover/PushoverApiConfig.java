package com.jitterted.mobreg.adapter.out.notification.pushover;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pushover.net.api")
public class PushoverApiConfig {

    private String token;
    private String groupKeyMob1; // key is: pushover.net.api.group-key-mob1

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGroupKeyMob1() {
        return groupKeyMob1;
    }

    public void setGroupKeyMob1(String groupKeyMob1) {
        this.groupKeyMob1 = groupKeyMob1;
    }
}
