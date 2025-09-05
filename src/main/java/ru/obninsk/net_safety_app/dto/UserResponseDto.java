package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserResponseDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    @JsonProperty("is_2fa_enabled")
    private Boolean is2faEnabled;
    @JsonProperty("is_activated")
    private Boolean isActivated;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
