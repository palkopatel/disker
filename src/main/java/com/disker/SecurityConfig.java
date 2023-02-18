package com.disker;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/home", "/login**","/callback/", "/webjars/**", "/error**", "/oauth2/authorization/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login();
    }
}