package org.oauth2workshop.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@EnableWebSecurity
class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomAuth2AccessTokenResponseClientConfiguration accessTokenResponseClient;

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public WebSecurityConfiguration(CustomAuth2AccessTokenResponseClientConfiguration accessTokenResponseClient, ClientRegistrationRepository clientRegistrationRepository) {
        this.accessTokenResponseClient = accessTokenResponseClient;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/login", "/login/oauth2/code/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .tokenEndpoint().accessTokenResponseClient(this.accessTokenResponseClient)
                .and()
                .authorizationEndpoint().authorizationRequestResolver(
                new CustomAuthorizationRequestResolverConfiguration(this.clientRegistrationRepository));
    }
}
