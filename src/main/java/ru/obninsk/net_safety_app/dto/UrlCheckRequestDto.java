package ru.obninsk.net_safety_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlCheckRequestDto {
    @NotBlank(message = "url field must not be empty")
    private String url;

    @JsonProperty("is_public")
    @NotNull(message = "is_public field must not be null")
    private Boolean isPublic;
}
