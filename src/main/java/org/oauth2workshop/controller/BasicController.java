package org.oauth2workshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
public class BasicController {

    private final WebClient webClient;

    @Autowired
    public BasicController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/spotify/artists")
    public Mono<String> getTopArtists(@RegisteredOAuth2AuthorizedClient(registrationId = "spotify") OAuth2AuthorizedClient client){
        Mono<String> resource = webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.scheme("https")
                                .host("api.spotify.com").path("/v1/me/top/artists")
                                .build())
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class);
        return resource.map(string -> "we retrieved the following resource using Oauth2: " + string + ". principal associated: " + client.getPrincipalName() + ". Token will expire at: " + client.getAccessToken()
                .getExpiresAt());
    }

    @GetMapping("/measures/{typeId}")
    public Mono<String> getWeight(@RegisteredOAuth2AuthorizedClient(registrationId = "withings") OAuth2AuthorizedClient client, @PathVariable int typeId) {
        Mono<String> resource = webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.scheme("https")
                                .host("wbsapi.withings.net").path("/measure")
                                .queryParam("action", "getmeas")
                                .queryParam("meastype", typeId)
                                .build())
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(String.class);
        return resource.map(string -> "we retrieved the following resource using Oauth2: " + string + ". principal associated: " + client.getPrincipalName() + ". Token will expire at: " + client.getAccessToken()
                .getExpiresAt());
    }
}
