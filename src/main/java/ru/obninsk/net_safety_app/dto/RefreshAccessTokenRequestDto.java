package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshAccessTokenRequestDto {
    @NotBlank(message = "refresh_token field must not be empty")
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String code;
}
