package org.oauth2workshop.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedditAccessTokenModel {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String scope;

    public Set<String> getScopes() {
        return Stream.of(scope.split("\\s+")).collect(Collectors.toSet());
    }
}
