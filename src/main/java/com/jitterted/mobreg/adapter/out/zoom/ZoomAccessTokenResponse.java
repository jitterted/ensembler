package com.jitterted.mobreg.adapter.out.zoom;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ZoomAccessTokenResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("expires_in")
        Long expiresIn,
        String scope) {
}
