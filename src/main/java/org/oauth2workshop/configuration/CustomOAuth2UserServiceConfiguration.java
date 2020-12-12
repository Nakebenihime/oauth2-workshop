package org.oauth2workshop.configuration;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
public class CustomOAuth2UserServiceConfiguration implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final WebClient client = WebClient.builder().build();
    @Value("${reddit.user-agent}")
    private String USER_AGENT;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        JSONObject attributes = client.get()
                .uri(oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(httpHeaders -> {
                    httpHeaders.setBearerAuth(oAuth2UserRequest.getAccessToken().getTokenValue());
                    httpHeaders.set(HttpHeaders.USER_AGENT, USER_AGENT);
                })
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, res -> {
                    log.info(res.statusCode().toString());
                    log.info(res.headers().asHttpHeaders().toString());
                    return res.createException();
                })
                .onStatus(HttpStatus::is5xxServerError, res -> {
                    log.info(res.statusCode().toString());
                    log.info(res.headers().asHttpHeaders().toString());
                    return res.createException();
                })
                .bodyToMono(JSONObject.class)
                .block();

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_OAUTH2"));

        return new DefaultOAuth2User(authorities, attributes, oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
    }
}
