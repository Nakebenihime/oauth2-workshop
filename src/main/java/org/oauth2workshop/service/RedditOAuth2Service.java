package org.oauth2workshop.service;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Slf4j
@Service
public class RedditOAuth2Service {

    private final WebClient webClient;

    @Autowired
    public RedditOAuth2Service(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<JSONObject> getPrefs(OAuth2AuthorizedClient client) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HttpScheme.HTTPS.toString())
                                .host("oauth.reddit.com").path("/api/v1/me/prefs")
                                .build())
                .attributes(oauth2AuthorizedClient(client))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, res -> {
                    log.info(res.statusCode().toString());
                    log.info(res.headers().asHttpHeaders().toString());
                    return res.createException();
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    log.info(response.statusCode().toString());
                    log.info(response.headers().asHttpHeaders().toString());
                    return response.createException();
                })

                .bodyToMono(JSONObject.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofMillis(300)));
    }

    public Mono<JSONObject> getPopularUsers(OAuth2AuthorizedClient client) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.scheme(HttpScheme.HTTPS.toString())
                                .host("oauth.reddit.com").path("/users/popular")
                                .build())
                .attributes(oauth2AuthorizedClient(client))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, res -> {
                    log.info(res.statusCode().toString());
                    log.info(res.headers().asHttpHeaders().toString());
                    return res.createException();
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    log.info(response.statusCode().toString());
                    log.info(response.headers().asHttpHeaders().toString());
                    return response.createException();
                })
                .bodyToMono(JSONObject.class)
                .retryWhen(Retry.fixedDelay(2, Duration.ofMillis(300)));
    }
}
