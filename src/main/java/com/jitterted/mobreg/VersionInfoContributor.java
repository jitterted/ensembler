package com.jitterted.mobreg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VersionInfoContributor implements InfoContributor {

    @Value("${app.version}")
    private String version;

    @Value("${app.name}")
    private String name;

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("version", Map.of("version", version, "name", name));
    }
}