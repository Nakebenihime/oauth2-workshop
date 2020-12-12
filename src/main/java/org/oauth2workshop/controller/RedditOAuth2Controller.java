package org.oauth2workshop.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.oauth2workshop.service.RedditOAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class RedditOAuth2Controller {

    private final RedditOAuth2Service redditOAuth2Service;

    @Autowired
    public RedditOAuth2Controller(RedditOAuth2Service redditOAuth2Service) {
        this.redditOAuth2Service = redditOAuth2Service;
    }

    @GetMapping("/api/v1/prefs")
    public Mono<JSONObject> getPrefs(@RegisteredOAuth2AuthorizedClient(registrationId = "reddit") OAuth2AuthorizedClient client) {
        return this.redditOAuth2Service.getPrefs(client);
    }

    @GetMapping("/api/v1/users/popular")
    public Mono<JSONObject> getPopularUsers(@RegisteredOAuth2AuthorizedClient(registrationId = "reddit") OAuth2AuthorizedClient client) {
        return this.redditOAuth2Service.getPopularUsers(client);
    }
}
