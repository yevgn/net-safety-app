package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuthResponseDto {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("is_2fa_enabled")
    private boolean is2faEnabled;
    @JsonProperty("secret_image_uri")
    private String secretImageUri;

    private String secret;
}
