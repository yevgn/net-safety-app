package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GigaChatResponseDto {
    private String content;
    @JsonProperty("is_auth_passed")
    private boolean isAuthPassed;
}
