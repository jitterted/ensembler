package com.jitterted.mobreg;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
//@ConditionalOnProperty(value = "ignore.security", havingValue = "true")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NoSecurityTestConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/**"
        );
    }
}
