package org.oauth2workshop.configuration;

import lombok.extern.slf4j.Slf4j;
import org.oauth2workshop.model.RedditAccessTokenModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Configuration
public class CustomAuth2AccessTokenResponseClientConfiguration implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final WebClient client = WebClient.builder().build();
    @Value("${reddit.user-agent}")
    private String USER_AGENT;

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest oAuth2AuthorizationCodeGrantRequest) {
        ClientRegistration clientRegistration = oAuth2AuthorizationCodeGrantRequest.getClientRegistration();
        OAuth2AuthorizationExchange oAuth2AuthorizationExchange = oAuth2AuthorizationCodeGrantRequest.getAuthorizationExchange();

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", clientRegistration.getAuthorizationGrantType().getValue());
        parameters.add("code", oAuth2AuthorizationExchange.getAuthorizationResponse().getCode());
        parameters.add("redirect_uri", oAuth2AuthorizationExchange.getAuthorizationResponse().getRedirectUri());

        RedditAccessTokenModel response = client.post()
                .uri(clientRegistration.getProviderDetails().getTokenUri())
                .headers(httpHeaders -> {
                    httpHeaders.setBasicAuth(clientRegistration.getClientId(), clientRegistration.getClientSecret());
                    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    httpHeaders.set(HttpHeaders.USER_AGENT, USER_AGENT);
                })
                .bodyValue(parameters)
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
                .bodyToMono(RedditAccessTokenModel.class)
                .block();

        Set<String> scopes = Objects.requireNonNull(response).getScopes().isEmpty() ? oAuth2AuthorizationCodeGrantRequest.getAuthorizationExchange().getAuthorizationRequest().getScopes() : response.getScopes();

        return OAuth2AccessTokenResponse
                .withToken(response.getAccessToken())
                .refreshToken(response.getRefreshToken())
                .expiresIn(response.getExpiresIn())
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .scopes(scopes)
                .build();
    }
}
