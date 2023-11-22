package com.jitterted.mobreg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

//@Configuration
//@ConditionalOnProperty(value = "ignore.security", havingValue = "true")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class NoSecurityTestConfiguration {
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring().requestMatchers("/**"
            );
        };
    }
}
